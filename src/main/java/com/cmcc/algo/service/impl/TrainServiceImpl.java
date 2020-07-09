package com.cmcc.algo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmcc.algo.common.builder.Builder;
import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.common.response.ResultCode;
import com.cmcc.algo.config.CommonConfig;
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

    @Autowired
    CommonConfig commonConfig;

    @Override
    public Boolean submitTrainTask(String federationUuid) {
        FederationEntity federation = federationMapper.findByUuid(federationUuid);
        if (commonConfig.getPartyId().intValue() != Integer.valueOf(federation.getGuest()).intValue()) {
            throw new APIException("wrong party id. expect " + commonConfig.getPartyId().toString());
        }

        if (federation.getStatus() != 1) {
            throw new APIException(ResultCode.NOT_FOUND,"联邦未处于就绪状态");
        }
        Algorithm algorithm = algorithmService.getOne(Wrappers.<Algorithm>lambdaQuery().eq(Algorithm::getId, federation.getAlgorithmId()));

        String label = Optional.ofNullable(JSONUtil.parseObj(federation.getDataFormat()).getStr("label")).orElseThrow(() -> new APIException(ResultCode.NOT_FOUND, "数据标签丢失"));
        Map<String, Object> algorithmParam = Convert.toMap(String.class, Object.class, JSONUtil.parseObj(federation.getParam()));

        List<UserFederation> usList = userFederationService.list(Wrappers.<UserFederation>lambdaQuery().eq(UserFederation::getFederationUUid, federationUuid));
        Integer guestId = null;
        List<Integer> hostIdList = new ArrayList<>();
        for (UserFederation userFederation : usList) {
            if (userFederation.getStatus().equals("0")) {
                hostIdList.add(userFederation.getUserId());
                continue;
            }
            if (userFederation.getStatus().equals("1")) {
                guestId = userFederation.getUserId();
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
//        String submitResponse = HttpUtil.post(fateUrl + URLConstant.JOB_SUBMIT_URL, JSONUtil.toJsonStr(submitRequest));
        // 模拟fate-flow 返回
        String submitResponse = "{\r\n    \"data\": {\r\n        \"board_url\": \"http://fateboard:8080/index.html#/dashboard?job_id=202007060000000000001&role=guest&party_id=9999\",\r\n        \"job_dsl_path\": \"/data/projects/fate/python/jobs/202007060000000000001/job_dsl.json\",\r\n        \"job_runtime_conf_path\": \"/data/projects/fate/python/jobs/202007060000000000001/job_runtime_conf.json\",\r\n        \"logs_directory\": \"/data/projects/fate/python/logs/202007060000000000001\",\r\n        \"model_info\": {\r\n            \"model_id\": \"arbiter-9999#guest-9999#host-9999#model\",\r\n            \"model_version\": \"202007060000000000001\"\r\n        }\r\n    },\r\n    \"jobId\": \"202007060000000000001\",\r\n    \"retcode\": 0,\r\n    \"retmsg\": \"success\"\r\n}";
        JSONObject submit = JSONUtil.parseObj(submitResponse);

        if (submit.getInt("retcode") != 0) {
            throw new APIException(ResultCode.NOT_FOUND, "任务提交失败", submit.getStr("retmsg"));
        }

        String queryRequest = JSONUtil.toJsonStr(new JSONObject().putOnce("job_id", submit.getStr("jobId")));
//        String queryResponse = HttpUtil.post(fateUrl + URLConstant.JOB_QUERY_URL, queryRequest);
        // 模拟fate-flow返回
        String queryResponse = "{\r\n    \"data\": [\r\n        {\r\n            \"f_create_time\": 1594024961000,\r\n            \"f_current_steps\": null,\r\n            \"f_current_tasks\": \"[\\\"202007060000000000001_intersection_0\\\"]\",\r\n            \"f_description\": \"\",\r\n            \"f_dsl\": \"{\\\"components\\\": {\\\"dataio_0\\\": {\\\"module\\\": \\\"DataIO\\\", \\\"input\\\": {\\\"data\\\": {\\\"data\\\": [\\\"args.train_data\\\"]}}, \\\"output\\\": {\\\"data\\\": [\\\"train\\\"], \\\"model\\\": [\\\"dataio\\\"]}}, \\\"intersection_0\\\": {\\\"module\\\": \\\"Intersection\\\", \\\"input\\\": {\\\"data\\\": {\\\"data\\\": [\\\"dataio_0.train\\\"]}}, \\\"output\\\": {\\\"data\\\": [\\\"train\\\"]}}, \\\"hetero_lr_0\\\": {\\\"module\\\": \\\"HeteroLR\\\", \\\"input\\\": {\\\"data\\\": {\\\"train_data\\\": [\\\"intersection_0.train\\\"]}}, \\\"output\\\": {\\\"data\\\": [\\\"train\\\"], \\\"model\\\": [\\\"hetero_lr\\\"]}}, \\\"evaluation_0\\\": {\\\"module\\\": \\\"Evaluation\\\", \\\"input\\\": {\\\"data\\\": {\\\"data\\\": [\\\"hetero_lr_0.train\\\"]}}, \\\"output\\\": {\\\"data\\\": [\\\"evaluate\\\"]}}}}\",\r\n            \"f_elapsed\": null,\r\n            \"f_end_time\": null,\r\n            \"f_initiator_party_id\": \"9999\",\r\n            \"f_is_initiator\": 1,\r\n            \"f_job_id\": \"202007060000000000001\",\r\n            \"f_name\": \"\",\r\n            \"f_party_id\": \"9999\",\r\n            \"f_progress\": 50,\r\n            \"f_role\": \"guest\",\r\n            \"f_roles\": \"{\\\"guest\\\": [9999], \\\"host\\\": [10000,10001], \\\"arbiter\\\": [9999]}\",\r\n            \"f_run_ip\": \"10.233.127.45:9380\",\r\n            \"f_runtime_conf\": \"{\\\"initiator\\\": {\\\"role\\\": \\\"guest\\\", \\\"party_id\\\": 9999}, \\\"job_parameters\\\": {\\\"work_mode\\\": 1, \\\"model_id\\\": \\\"arbiter-9999#guest-9999#host-9999#model\\\", \\\"model_version\\\": \\\"202007060000000000001\\\"}, \\\"role\\\": {\\\"guest\\\": [9999], \\\"host\\\": [10000,10001], \\\"arbiter\\\": [9999]}, \\\"role_parameters\\\": {\\\"guest\\\": {\\\"args\\\": {\\\"data\\\": {\\\"train_data\\\": [{\\\"name\\\": \\\"hetero_breast_b\\\", \\\"namespace\\\": \\\"hetero_breast_guest\\\"}], \\\"eval_data\\\": [{\\\"name\\\": \\\"hetero_breast_b\\\", \\\"namespace\\\": \\\"hetero_breast_guest\\\"}]}}, \\\"dataio_0\\\": {\\\"with_label\\\": [true], \\\"label_name\\\": [\\\"y\\\"], \\\"label_type\\\": [\\\"int\\\"], \\\"output_format\\\": [\\\"dense\\\"], \\\"missing_fill\\\": [true], \\\"outlier_replace\\\": [true]}, \\\"evaluation_0\\\": {\\\"eval_type\\\": [\\\"binary\\\"], \\\"pos_label\\\": [1]}}, \\\"host\\\": {\\\"args\\\": {\\\"data\\\": {\\\"train_data\\\": [{\\\"name\\\": \\\"hetero_breast_a\\\", \\\"namespace\\\": \\\"hetero_breast_host\\\"}], \\\"eval_data\\\": [{\\\"name\\\": \\\"hetero_breast_a\\\", \\\"namespace\\\": \\\"hetero_breast_host\\\"}]}}, \\\"dataio_0\\\": {\\\"with_label\\\": [false], \\\"output_format\\\": [\\\"dense\\\"], \\\"outlier_replace\\\": [true]}, \\\"evaluation_0\\\": {\\\"need_run\\\": [false]}}}, \\\"algorithm_parameters\\\": {\\\"hetero_lr_0\\\": {\\\"penalty\\\": \\\"L2\\\", \\\"optimizer\\\": \\\"nesterov_momentum_sgd\\\", \\\"tol\\\": 0.0001, \\\"alpha\\\": 0.01, \\\"max_iter\\\": 30, \\\"early_stop\\\": \\\"weight_diff\\\", \\\"batch_size\\\": -1, \\\"learning_rate\\\": 0.15, \\\"init_param\\\": {\\\"init_method\\\": \\\"random_uniform\\\"}, \\\"sqn_param\\\": {\\\"update_interval_L\\\": 3, \\\"memory_M\\\": 5, \\\"sample_size\\\": 5000, \\\"random_seed\\\": null}, \\\"cv_param\\\": {\\\"n_splits\\\": 5, \\\"shuffle\\\": false, \\\"random_seed\\\": 103, \\\"need_cv\\\": false, \\\"evaluate_param\\\": {\\\"eval_type\\\": \\\"binary\\\"}}}, \\\"intersect_0\\\": {\\\"intersect_method\\\": \\\"rsa\\\", \\\"sync_intersect_ids\\\": true, \\\"only_output_key\\\": false}}}\",\r\n            \"f_start_time\": 1594024964000,\r\n            \"f_status\": \"running\",\r\n            \"f_tag\": \"\",\r\n            \"f_train_runtime_conf\": \"{}\",\r\n            \"f_update_time\": 1594024993000,\r\n            \"f_work_mode\": 1\r\n        }\r\n    ],\r\n    \"retcode\": 0,\r\n    \"retmsg\": \"success\"\r\n}";
        List<JSONObject> roleJobList = JSONUtil.parseObj(queryResponse).getJSONArray("data").toList(JSONObject.class);
        // 默认最多一个guest，未对异常做处理
        JSONObject query = roleJobList.stream().filter(x -> x.getStr("f_role").equals("guest")).limit(1).collect(Collectors.toList()).get(0);

        // 将数据集以及算法参数等参数放在一个map中，返回到train_param中
        Map<String, Object> trainParam = new HashMap<>();
        List<Integer> partyList = ListUtil.toList(hostIdList);
        partyList.add(guestId);
        /*
        List<FederationDataset> federationDatasetList = federationDatasetService.list(Wrappers.<FederationDataset>lambdaQuery()
                .eq(FederationDataset::getFederationUuid, federationUuid)
                .in(FederationDataset::getPartyId, hostIdList));
        */
        List<FederationDataset> federationDatasetList = federationDatasetRepository.findByFederationUuidAndPartyIdIn(federationUuid, partyList);

        trainParam.put("dataset", federationDatasetList);
        trainParam.putAll(algorithmParam);

        JSONObject model = submit.getJSONObject("data").getJSONObject("model_info");

        Train train = Builder.of(Train::new)
                .with(Train::setUuid, IdUtil.randomUUID().replaceAll("-", ""))
                .with(Train::setFederationUuid, federationUuid)
                .with(Train::setStatus, 0)
                .with(Train::setJobUrl, JSONUtil.parseObj(submit.getStr("data")).getStr("board_url"))
                .with(Train::setStartTime, TimeUtils.getTimeStrByTimestamp(query.getLong("f_start_time")))
                .with(Train::setStartTimestamp, query.getLong("f_start_time"))
                .with(Train::setDuration, TimeUtils.getDurationStrByTimestamp(System.currentTimeMillis() - query.getLong("f_start_time")))
                .with(Train::setAlgorithmId, federation.getAlgorithmId())
                .with(Train::setJobId, submit.getStr("jobId"))
                .with(Train::setTrainParam, JSONUtil.toJsonStr(trainParam))
                .with(Train::setModel, JSONUtil.toJsonStr(model))
                .build();
        trainService.save(train);

        // 修改联邦状态
        federation.setStatus(2);
        federationMapper.save(federation);

        return true;
    }
}
