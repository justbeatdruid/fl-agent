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

    @Autowired
    CommonConfig commonConfig;

    @Override
    public Boolean submitPredictTask(String federationUuid) {
        FederationEntity federation = federationMapper.findByUuid(federationUuid);
        if (commonConfig.getPartyId().intValue() != Integer.valueOf(federation.getGuest()).intValue()) {
            throw new APIException("wrong party id. expect " + commonConfig.getPartyId().toString());
        }

        if (federation.getStatus() != 1) {
            throw new APIException(ResultCode.NOT_FOUND,"联邦未处于就绪状态");
        }

        // 从最新一次train记录获取param
        Train train = Optional.ofNullable(trainService.getOne(Wrappers.<Train>lambdaQuery()
                .eq(Train::getFederationUuid, federationUuid)
                .orderByDesc(Train::getStartTime)
                .last("limit 1")))
                .orElseThrow(() -> new APIException(ResultCode.NOT_FOUND, "尚未进行一次训练"));
        JSONObject predictParam = JSONUtil.parseObj(train.getTrainParam());
        JSONObject model = JSONUtil.parseObj(train.getModel());
        predictParam.putAll(model);
        predictParam.remove("data_type");
        predictParam.putOnce("data_type", 1);

        String predictConf = TemplateUtils.useTemplate(predictParam, "predict_conf.ftl");
        JSONObject submitRequest = new JSONObject().putOnce("job_runtime_conf", predictConf);

        String fateUrl = "http://" + FateFlowConfig.fateFlowHost + ":" + FateFlowConfig.fateFlowPort;
//        String submitResponse = HttpUtil.post(fateUrl + URLConstant.JOB_SUBMIT_URL, JSONUtil.toJsonStr(submitRequest));
        // 模拟返回
        String submitResponse = "{\r\n    \"data\": {\r\n        \"board_url\": \"http://fateboard:8080/index.html#/dashboard?job_id=202007060000000000002&role=guest&party_id=9999\",\r\n        \"job_dsl_path\": \"/data/projects/fate/python/jobs/202007060000000000002/job_dsl.json\",\r\n        \"job_runtime_conf_path\": \"/data/projects/fate/python/jobs/202007060000000000002/job_runtime_conf.json\",\r\n        \"logs_directory\": \"/data/projects/fate/python/logs/202007060000000000002\",\r\n        \"model_info\": {\r\n            \"model_id\": \"arbiter-9999#guest-9999#host-9999#model\",\r\n            \"model_version\": \"202007060000000000002\"\r\n        }\r\n    },\r\n    \"jobId\": \"202007060000000000002\",\r\n    \"retcode\": 0,\r\n    \"retmsg\": \"success\"\r\n}";
        JSONObject submit = JSONUtil.parseObj(submitResponse);

        if (submit.getInt("retcode") != 0) {
            throw new APIException(ResultCode.NOT_FOUND, "任务提交失败", submit.getStr("retmsg"));
        }

        String queryRequest = JSONUtil.toJsonStr(new JSONObject().putOnce("job_id", submit.getStr("jobId")));
//        String queryResponse = HttpUtil.post(fateUrl + URLConstant.JOB_QUERY_URL, queryRequest);
        // 模拟返回
        String queryResponse = "{\r\n    \"data\": [\r\n        {\r\n            \"f_create_time\": 1594025861000,\r\n            \"f_current_steps\": null,\r\n            \"f_current_tasks\": \"[\\\"202007060000000000002_algorithm_0\\\"]\",\r\n            \"f_description\": \"\",\r\n            \"f_dsl\": \"{\\\"components\\\": {\\\"dataio_0\\\": {\\\"module\\\": \\\"DataIO\\\", \\\"input\\\": {\\\"model\\\": [\\\"pipeline.dataio_0.dataio\\\"], \\\"data\\\": {\\\"data\\\": [\\\"args.eval_data\\\"]}}, \\\"output\\\": {\\\"data\\\": [\\\"train\\\"]}, \\\"CodePath\\\": \\\"federatedml/util/data_io.py/DataIO\\\"}, \\\"intersection_0\\\": {\\\"module\\\": \\\"Intersection\\\", \\\"output\\\": {\\\"data\\\": [\\\"train\\\"]}, \\\"input\\\": {\\\"data\\\": {\\\"data\\\": [\\\"dataio_0.train\\\"]}}, \\\"CodePath\\\": \\\"federatedml/statistic/intersect/intersect_model.py/IntersectGuest\\\"}, \\\"algorithm_0\\\": {\\\"module\\\": \\\"HeteroLR\\\", \\\"input\\\": {\\\"model\\\": [\\\"pipeline.algorithm_0.hetero_lr\\\"], \\\"data\\\": {\\\"eval_data\\\": [\\\"intersection_0.train\\\"]}}, \\\"output\\\": {\\\"data\\\": [\\\"train\\\"]}, \\\"CodePath\\\": \\\"federatedml/linear_model/logistic_regression/hetero_logistic_regression/hetero_lr_guest.py/HeteroLRGuest\\\"}}}\",\r\n            \"f_elapsed\": 35028,\r\n            \"f_end_time\": 1594025897000,\r\n            \"f_initiator_party_id\": \"9999\",\r\n            \"f_is_initiator\": 1,\r\n            \"f_job_id\": \"202007060000000000002\",\r\n            \"f_name\": \"\",\r\n            \"f_party_id\": \"9999\",\r\n            \"f_progress\": 100,\r\n            \"f_role\": \"guest\",\r\n            \"f_roles\": \"{\\\"guest\\\": [9999], \\\"host\\\": [9999], \\\"arbiter\\\": [9999]}\",\r\n            \"f_run_ip\": \"10.233.127.45:9380\",\r\n            \"f_runtime_conf\": \"{\\\"initiator\\\": {\\\"role\\\": \\\"guest\\\", \\\"party_id\\\": 9999}, \\\"job_parameters\\\": {\\\"work_mode\\\": 1, \\\"job_type\\\": \\\"predict\\\", \\\"model_id\\\": \\\"arbiter-9999#guest-9999#host-9999#model\\\", \\\"model_version\\\": \\\"202007060000000000002\\\"}, \\\"role\\\": {\\\"guest\\\": [9999], \\\"host\\\": [9999], \\\"arbiter\\\": [9999]}, \\\"role_parameters\\\": {\\\"guest\\\": {\\\"args\\\": {\\\"data\\\": {\\\"eval_data\\\": [{\\\"name\\\": \\\"hetero_breast_b\\\", \\\"namespace\\\": \\\"hetero_breast_guest\\\"}]}}}, \\\"host\\\": {\\\"args\\\": {\\\"data\\\": {\\\"eval_data\\\": [{\\\"name\\\": \\\"hetero_breast_a\\\", \\\"namespace\\\": \\\"hetero_breast_host\\\"}]}}}}}\",\r\n            \"f_start_time\": 1594025865000,\r\n            \"f_status\": \"success\",\r\n            \"f_tag\": \"\",\r\n            \"f_train_runtime_conf\": \"{\\\"initiator\\\": {\\\"role\\\": \\\"guest\\\", \\\"party_id\\\": 9999}, \\\"job_parameters\\\": {\\\"work_mode\\\": 1, \\\"model_id\\\": \\\"arbiter-9999#guest-9999#host-9999#model\\\", \\\"model_version\\\": \\\"2020070706091032122530\\\"}, \\\"role\\\": {\\\"guest\\\": [9999], \\\"host\\\": [9999], \\\"arbiter\\\": [9999]}, \\\"role_parameters\\\": {\\\"guest\\\": {\\\"args\\\": {\\\"data\\\": {\\\"train_data\\\": [{\\\"name\\\": \\\"hetero_breast_b\\\", \\\"namespace\\\": \\\"hetero_breast_guest\\\"}], \\\"eval_data\\\": [{\\\"name\\\": \\\"hetero_breast_b\\\", \\\"namespace\\\": \\\"hetero_breast_guest\\\"}]}}, \\\"dataio_0\\\": {\\\"with_label\\\": [true], \\\"label_name\\\": [\\\"y\\\"], \\\"label_type\\\": [\\\"int\\\"], \\\"output_format\\\": [\\\"dense\\\"], \\\"missing_fill\\\": [true], \\\"outlier_replace\\\": [true]}, \\\"evaluation_0\\\": {\\\"eval_type\\\": [\\\"binary\\\"], \\\"pos_label\\\": [1]}}, \\\"host\\\": {\\\"args\\\": {\\\"data\\\": {\\\"train_data\\\": [{\\\"name\\\": \\\"hetero_breast_a\\\", \\\"namespace\\\": \\\"hetero_breast_host\\\"}], \\\"eval_data\\\": [{\\\"name\\\": \\\"hetero_breast_a\\\", \\\"namespace\\\": \\\"hetero_breast_host\\\"}]}}, \\\"dataio_0\\\": {\\\"with_label\\\": [false], \\\"output_format\\\": [\\\"dense\\\"], \\\"outlier_replace\\\": [true]}, \\\"evaluation_0\\\": {\\\"need_run\\\": [false]}}}, \\\"algorithm_parameters\\\": {\\\"algorithm_0\\\": {\\\"penalty\\\": \\\"L2\\\", \\\"optimizer\\\": \\\"nesterov_momentum_sgd\\\", \\\"tol\\\": 0.0001, \\\"alpha\\\": 0.01, \\\"max_iter\\\": 30, \\\"early_stop\\\": \\\"weight_diff\\\", \\\"batch_size\\\": -1, \\\"learning_rate\\\": 0.15, \\\"init_param\\\": {\\\"init_method\\\": \\\"random_uniform\\\"}, \\\"sqn_param\\\": {\\\"update_interval_L\\\": 3, \\\"memory_M\\\": 5, \\\"sample_size\\\": 5000, \\\"random_seed\\\": null}, \\\"cv_param\\\": {\\\"n_splits\\\": 5, \\\"shuffle\\\": false, \\\"random_seed\\\": 103, \\\"need_cv\\\": false, \\\"evaluate_param\\\": {\\\"eval_type\\\": \\\"binary\\\"}}}, \\\"intersect_0\\\": {\\\"intersect_method\\\": \\\"rsa\\\", \\\"sync_intersect_ids\\\": true, \\\"only_output_key\\\": false}}}\",\r\n            \"f_update_time\": 1594025897000,\r\n            \"f_work_mode\": 1\r\n        }\r\n    ],\r\n    \"retcode\": 0,\r\n    \"retmsg\": \"success\"\r\n}";
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

        String[] cmd = {commonConfig.getPythonPath(), commonConfig.getCliPyPath(), "-f", "component_output_data",
                "-j", predict.getJobId(), "-r", "guest", "-p", federationEntity.getGuest(),
                "-cpn", "algorithm_0", "-o", commonConfig.getFilePath()};
        String execResponse = RuntimeUtil.execForStr(cmd);

        if (JSONUtil.parseObj(execResponse).getInt("retcode") != 0) {
            throw new APIException(ResultCode.NOT_FOUND, "保存失败");
        }

        return JSONUtil.parseObj(execResponse).getStr("directory");
    }
}
