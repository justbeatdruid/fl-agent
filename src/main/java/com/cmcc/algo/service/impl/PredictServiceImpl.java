package com.cmcc.algo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmcc.algo.common.builder.Builder;
import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.common.response.ResultCode;
import com.cmcc.algo.config.CommonConfig;
import com.cmcc.algo.config.FateFlowConfig;
import com.cmcc.algo.constant.URLConstant;
import com.cmcc.algo.entity.*;
import com.cmcc.algo.mapper.FederationRepository;
import com.cmcc.algo.mapper.PredictMapper;
import com.cmcc.algo.service.IAlgorithmService;
import com.cmcc.algo.service.IFederationService;
import com.cmcc.algo.service.IPredictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcc.algo.service.ITrainService;
import com.cmcc.algo.util.TemplateUtils;
import com.cmcc.algo.util.TimeUtils;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PredictServiceImpl extends ServiceImpl<PredictMapper, Predict> implements IPredictService {
    @Autowired
    ITrainService trainService;

    @Autowired
    IPredictService predictService;

    @Autowired
    FederationRepository federationMapper;

    @Autowired
    IAlgorithmService algorithmService;

    @Override
    public Boolean submitPredictTask(String federationUuid) {
        FederationEntity federation = federationMapper.getOne(Long.valueOf(federationUuid));

        // 从最新一次train记录获取param
        Train train = Optional.ofNullable(trainService.getOne(Wrappers.<Train>lambdaQuery()
                .eq(Train::getFederationUuid, federationUuid)
                .orderByDesc(Train::getStartTime)
                .last("limit 1")))
                .orElseThrow(() -> new APIException(ResultCode.NOT_FOUND, "尚未进行一次训练"));
        JSONObject predictParam = JSONUtil.parseObj(train.getTrainParam());
        JSONObject model = JSONUtil.parseObj(train.getModel());
        predictParam.putAll(model);
        predictParam.putOnce("data_type", 1);

        String fateUrl = "http://" + FateFlowConfig.fateFlowHost + ":" + FateFlowConfig.fateFlowPort;
        String predictConf = TemplateUtils.useTemplate(predictParam, "predict_conf.ftl");

        JSONObject submitRequest = new JSONObject();
        submitRequest.putOnce("job_runtime_conf", predictConf);
        String submitResponse = HttpUtil.post(fateUrl + URLConstant.JOB_SUBMIT_URL, JSONUtil.toJsonStr(submitRequest));
        JSONObject submit = JSONUtil.parseObj(submitResponse);

        if (submit.getInt("retcode") != 0) {
            throw new APIException(ResultCode.NOT_FOUND, "任务提交失败", submit.getStr("retmsg"));
        }

        String queryRequest = JSONUtil.toJsonStr(new JSONObject().putOnce("job_id", submit.getStr("jobId")));
        String queryResponse = HttpUtil.post(fateUrl + URLConstant.JOB_QUERY_URL, queryRequest);
        List<JSONObject> roleJobList = JSONUtil.parseObj(queryResponse).getJSONArray("data").toList(JSONObject.class);
        // 默认最多一个guest，未对异常做处理
        JSONObject query = roleJobList.stream().filter(x -> x.getStr("f_role").equals("guest")).limit(1).collect(Collectors.toList()).get(0);

        Predict predict = Builder.of(Predict::new)
                .with(Predict::setUuid, IdUtil.randomUUID())
                .with(Predict::setFederationUuid, federationUuid)
                .with(Predict::setTrainUuid, train.getUuid())
                .with(Predict::setAlgorithmId, train.getAlgorithmId())
                .with(Predict::setStatus, 0)
                .with(Predict::setJobUrl, JSONUtil.parseObj(submit.getStr("data")).getStr("board_url"))
                .with(Predict::setStartTime, TimeUtils.getTimeStrByTimestamp(query.getLong("f_start_time")))
                .with(Predict::setStartTimestamp, query.getLong("f_start_time"))
                .with(Predict::setDuration, TimeUtils.getDurationStrByTimestamp(System.currentTimeMillis() - query.getLong("f_start_time")))
                .with(Predict::setPredictParam, JSONUtil.toJsonStr(predictParam))
                .with(Predict::setJobId, submit.getStr("jobId"))
                .build();

        predictService.save(predict);

        // 修改联邦状态
        federation.setStatus(2);
        federationMapper.save(federation);

        return true;
    }

    @Override
    public String exportResult(String predictUuid) {
        Predict predict = predictService.getOne(Wrappers.<Predict>lambdaQuery().eq(Predict::getUuid, predictUuid));
        if (predict.getStatus() == 0) {
            throw new APIException(ResultCode.NOT_FOUND,"该预测尚未完成，无法导出结果");
        }

        FederationEntity federationEntity = federationMapper.findByUuid(predict.getFederationUuid());
        Algorithm algorithm = algorithmService.getOne(Wrappers.<Algorithm>lambdaQuery().eq(Algorithm::getId, federationEntity.getAlgorithmId()));

        if (!ObjectUtil.isAllNotEmpty(predict, federationEntity, algorithm)) {
            throw new APIException(ResultCode.NOT_FOUND,"预测记录查询错误");
        }

        // linux
        String[] cmd = {CommonConfig.pythonPath, CommonConfig.cliPyPath, "-f", "component_output_data",
                "-j", predict.getJobId(), "-r", "guest", "-p", federationEntity.getGuest(),
                "-cpn", algorithm.getAlgorithmComponent(), "-o", CommonConfig.filePath};
        // windows
//        String [] cmd={"cmd","/C","copy exe1 exe2"};
        String execResponse = RuntimeUtil.execForStr(cmd);
        if (JSONUtil.parseObj(execResponse).getInt("retcode") != 0) {
            throw new APIException(ResultCode.NOT_FOUND, "保存失败");
        }

        return JSONUtil.parseObj(execResponse).getStr("directory");
    }
}
