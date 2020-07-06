package com.cmcc.algo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmcc.algo.common.builder.Builder;
import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.common.response.ResultCode;
import com.cmcc.algo.config.FateFlowConfig;
import com.cmcc.algo.constant.CommonConstant;
import com.cmcc.algo.constant.URLConstant;
import com.cmcc.algo.entity.*;
import com.cmcc.algo.mapper.FederationDatasetRepository;
import com.cmcc.algo.mapper.FederationRepository;
import com.cmcc.algo.mapper.TrainMapper;
import com.cmcc.algo.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcc.algo.util.TemplateUtils;
import com.cmcc.algo.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements ITrainService {
    @Autowired
    IDatasetService datasetService;

    @Autowired
    FederationRepository federationMapper;

    @Autowired
    IUserFederationService userFederationService;

    //@Autowired
    //IFederationDatasetService federationDatasetService;

    @Autowired
    IAlgorithmService algorithmService;

    @Autowired
    ITrainService trainService;

    @Autowired
    FederationDatasetRepository federationDatasetRepository;

    @Override
    public Boolean submitTrainTask(String federationUuid) {
        FederationEntity federation = federationMapper.getOne(Long.valueOf(federationUuid));
        Algorithm algorithm = algorithmService.getOne(Wrappers.<Algorithm>lambdaQuery().eq(Algorithm::getId, federation.getAlgorithmId()));

        String label = Optional.ofNullable(JSONUtil.parseObj(federation.getDataFormat()).getStr("label")).orElseThrow(() -> new APIException(ResultCode.NOT_FOUND, "数据标签丢失"));
        Map<String, Object> algorithmParam = BeanUtil.beanToMap(JSONUtil.parseObj(federation.getParam()));

        List<UserFederation> usList = userFederationService.list(Wrappers.<UserFederation>lambdaQuery().eq(UserFederation::getFederationUUid, federationUuid));
        Integer guestId = null;
        List<Integer> hostIdList = new ArrayList<>();
        for (UserFederation userFederation : usList) {
            if (userFederation.getStatus().equals("0")) {
                hostIdList.add(userFederation.getUserId());
                continue;
            }
            if (userFederation.getStatus().equals("1")) {
                guestId = userFederation.getId();
                continue;
            }
        }
        if (guestId == null) {
            throw new APIException(ResultCode.NOT_FOUND, "联邦创建者丢失");
        }
        algorithmParam.put("guest_party_id", guestId);
        algorithmParam.put("host_party_id_list", hostIdList);
        algorithmParam.put("federation_id", federationUuid);
        algorithmParam.put("data_type", 0);
        algorithmParam.put("label_name", label);

        String fateUrl = "http://" + FateFlowConfig.fateFlowHost + ":" + FateFlowConfig.fateFlowPort;

        // 如果要修改为统一模板，需要确认纵向存在对齐步骤，每个算法名不同，参数也可能存在不同
        String AlgDsl = TemplateUtils.useTemplate(null, algorithm.getAlgorithmName() + "_dsl.ftl");
        String AlgConf = TemplateUtils.useTemplate(algorithmParam, algorithm.getAlgorithmName() + "_conf.ftl");

        JSONObject submitRequest = new JSONObject();
        submitRequest.putOnce("job_dsl", AlgDsl).putOnce("job_runtime_conf", AlgConf);
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


        // 将数据集以及算法参数等参数放在一个map中，返回到train_param中
        Map<String, Object> trainParam = new HashMap<>();
        hostIdList.add(guestId);
        /*
        List<FederationDataset> federationDatasetList = federationDatasetService.list(Wrappers.<FederationDataset>lambdaQuery()
                .eq(FederationDataset::getFederationUuid, federationUuid)
                .in(FederationDataset::getPartyId, hostIdList));
        */
        List<FederationDataset> federationDatasetList = federationDatasetRepository.findByFederationUuidAndPartyIdIn(federationUuid, hostIdList);

        trainParam.put("dataset", federationDatasetList);
        trainParam.putAll(algorithmParam);

        JSONObject model = submit.getJSONObject("data").getJSONObject("model_info");

        Train train = Builder.of(Train::new)
                .with(Train::setUuid, IdUtil.randomUUID())
                .with(Train::setFederationUuid, federationUuid)
                .with(Train::setStatus, 0)
                .with(Train::setJobUrl, JSONUtil.parseObj(submit.getStr("data")).getStr("board_url"))
                .with(Train::setStartTime, TimeUtils.getTimeStrByTimestamp(query.getLong("f_start_time")))
                .with(Train::setStartTimestamp, query.getLong("f_start_time"))
                .with(Train::setDuration, TimeUtils.getDurationStrByTimestamp(System.currentTimeMillis()-query.getLong("f_start_time")))
                .with(Train::setAlgorithmId, federation.getAlgorithmId())
                .with(Train::setJobId, submit.getStr("jobId"))
                .with(Train::setTrainParam, JSONUtil.toJsonStr(trainParam))
                .with(Train::setModel,JSONUtil.toJsonStr(model))
                .build();
        trainService.save(train);

        // 修改联邦状态
        federation.setStatus(2);
        federationMapper.save(federation);

        return true;
    }
}
