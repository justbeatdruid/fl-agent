package com.cmcc.algo.task;

import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.ObjectUtil;
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
import com.cmcc.algo.service.IFederationService;
import com.cmcc.algo.service.IPredictService;
import com.cmcc.algo.service.ITrainService;
import com.cmcc.algo.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Component
@Order(value = 1)
@Slf4j
public class SynchronizeStatusTask implements ApplicationRunner {
    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    ITrainService trainService;

    @Autowired
    FederationRepository federationMapper;

    @Autowired
    IPredictService predictService;

    @Value("${time-interval}")
    private Integer interval;

    @Bean
    public static ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        log.info("new ThreadPoolTaskScheduler in status task");
        return new ThreadPoolTaskScheduler();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        synchronizeTaskStatus();
    }

    private void synchronizeTaskStatus() {
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);

        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    List<FedTrain> nfFedTrainList = getNfFedTrainList();
                    updateTrainJob(nfFedTrainList);
                } catch (Exception e){
                    log.warn("synchronize is failed, the error detail is {}", e.getMessage());
                }

                try {
                    List<FedPredict> nfFedPredictList = getNfPredictList();
                    updatePredictJob(nfFedPredictList);
                } catch (Exception e){
                    log.warn("synchronize is failed, the error detail is {}", e.getMessage());
                }
            }
        }, new CronTrigger("0 */"+ interval +" * * * *"));
    }

    private List<FedTrain> getNfFedTrainList() {
        List<Train> nfTrainList = trainService.list(Wrappers.<Train>lambdaQuery().eq(Train::getStatus, 0));
        List<FederationEntity> nfTrainFederationList = federationMapper.findByUuidIn(nfTrainList.stream().map(x -> x.getFederationUuid()).collect(Collectors.toList()));

        List<FedTrain> nfFedTrainList = new ArrayList<>();
        for (Train train : nfTrainList) {
            FedTrain fedTrain = Builder.of(FedTrain::new)
                    .with(FedTrain::setFederationUuid, train.getFederationUuid())
                    .with(FedTrain::setTrain, train)
                    .with(FedTrain::setFederationEntity, nfTrainFederationList.stream()
                            .filter(x -> x.getUuid().equals(train.getFederationUuid()))
                            .limit(1)
                            .collect(Collectors.toList())
                            .get(0))
                    .build();
            nfFedTrainList.add(fedTrain);
        }
        return nfFedTrainList;
    }

    private void updateTrainJob(List<FedTrain> fedTrainList) {
        for (FedTrain fedTrain : fedTrainList) {
            FederationEntity fed = fedTrain.getFederationEntity();
            Train train = fedTrain.getTrain();

            log.info("begin to query train job of federation {}", fedTrain.getFederationUuid());
            String jobQueryURL = "http://" + FateFlowConfig.fateFlowHost + ":" + FateFlowConfig.fateFlowPort + URLConstant.JOB_QUERY_URL;
            String jobQueryParam = JSONUtil.toJsonStr(new JSONObject().putOnce("job_id", train.getJobId()));

            String jobQueryResponse = HttpUtil.post(jobQueryURL, jobQueryParam);

            if (JSONUtil.parseObj(jobQueryResponse).getInt("retcode") != 0) {
                log.warn("query job {} is failed, the error detail is {}", train.getJobId(), JSONUtil.parseObj(jobQueryResponse).getStr("retmsg"));
                throw new APIException(ResultCode.NOT_FOUND,"任务查询失败");
            }
            List<JSONObject> roleJobList = JSONUtil.parseObj(jobQueryResponse).getJSONArray("data").toList(JSONObject.class);
            JSONObject query = roleJobList.stream().filter(x -> x.getStr("f_role").equals("guest")).limit(1).collect(Collectors.toList()).get(0);

            log.info("begin to update train job status of federation {}", fedTrain.getFederationUuid());
            switch (query.getStr("f_status")) {
                case "running":
                    train.setDuration(TimeUtils.getDurationStrByTimestamp(System.currentTimeMillis() - train.getStartTimestamp()));
                    trainService.updateById(train);
                    log.info("job {} is still running", query.getStr("f_job_id"));
                    break;
                case "success":
                    // 标记状态
                    fed.setStatus(3);
                    train.setStatus(1);

                    // 更新持续时间
                    train.setDuration(TimeUtils.getDurationStrByTimestamp(query.getLong("f_end_time") - train.getStartTimestamp()));
                    // 获取评价指标, 此处需将算法模板中算法统一成algorithm_0
                    JSONObject trainData = query.getJSONObject("train").getJSONObject("algorithm_0");
                    // TODO 测试每一个元素是JSONArray还是Tuple
                    for (Tuple data : trainData.getJSONArray("data").toList(Tuple.class)) {
                        if (String.valueOf(data.get(0)).equals("auc")) {
                            train.setAuc((Float) data.get(1));
                        }
                        if (String.valueOf(data.get(0)).equals("ks")) {
                            train.setKs((Float) data.get(1));
                        }
                    }
                    if (!ObjectUtil.isAllNotEmpty(train.getAuc(), train.getKs())) {
                        log.warn("getting AUC or KS from train job is failed");
                    }

                    List<Tuple> accuracyList = query.getJSONObject("train").getJSONObject("algorithm_0_accuracy").getJSONArray("data").toList(Tuple.class);
                    Optional<Float> maximumAccuracy =  accuracyList.stream().map(x -> (Float) x.get(1)).max(((o1, o2) -> o1.compareTo(o2)));
                    if (maximumAccuracy.get() == null) {
                        log.warn("getting accuracy from train job is failed");
                    }
                    train.setAccuracy(maximumAccuracy.get());

                    federationMapper.save(fed);
                    trainService.updateById(train);
                    log.info("job {} is success", query.getStr("f_job_id"));
                    break;
                case "failed":
                    // 标记状态
                    fed.setStatus(4);
                    train.setStatus(2);

                    // 失败时可能没有结束时间，因此不作更新
                    log.info("job {} is failed", query.getStr("f_job_id"));
                    break;
                default:
                    log.warn("there is an unknown status {}", query.getStr("f_status"));
                    break;
            }
        }
    }

    private List<FedPredict> getNfPredictList(){
        List<Predict> nfPredictList = predictService.list(Wrappers.<Predict>lambdaQuery().eq(Predict::getStatus, 0));
        List<FederationEntity> nfPredictFederationList = federationMapper.findByUuidIn(nfPredictList.stream().map(x -> x.getFederationUuid()).collect(Collectors.toList()));

        List<FedPredict> nfFedPredictList = new ArrayList<>();
        for (Predict predict : nfPredictList) {
            FedPredict fedPredict = Builder.of(FedPredict::new)
                    .with(FedPredict::setFederationUuid, predict.getFederationUuid())
                    .with(FedPredict::setPredict, predict)
                    .with(FedPredict::setFederationEntity, nfPredictFederationList.stream()
                            .filter(x -> x.getUuid().equals(predict.getFederationUuid()))
                            .limit(1)
                            .collect(Collectors.toList())
                            .get(0))
                    .build();
            nfFedPredictList.add(fedPredict);
        }
        return nfFedPredictList;
    }

    private void updatePredictJob(List<FedPredict> fedPredictList) {
        for (FedPredict fedPredict : fedPredictList) {
            FederationEntity fed = fedPredict.getFederationEntity();
            Predict predict = fedPredict.getPredict();

            log.info("begin to query predict job of federation {}", fedPredict.getFederationUuid());
            String jobQueryURL = "http://" + FateFlowConfig.fateFlowHost + ":" + FateFlowConfig.fateFlowPort + URLConstant.JOB_QUERY_URL;
            String jobQueryParam = JSONUtil.toJsonStr(new JSONObject().putOnce("job_id", predict.getJobId()));

            String jobQueryResponse = HttpUtil.post(jobQueryURL, jobQueryParam);

            if (JSONUtil.parseObj(jobQueryResponse).getInt("retcode") != 0) {
                log.warn("query job {} is failed, the error detail is {}", predict.getJobId(), JSONUtil.parseObj(jobQueryResponse).getStr("retmsg"));
                throw new APIException(ResultCode.NOT_FOUND,"任务查询失败");
            }
            List<JSONObject> roleJobList = JSONUtil.parseObj(jobQueryResponse).getJSONArray("data").toList(JSONObject.class);
            JSONObject query = roleJobList.stream().filter(x -> x.getStr("f_role").equals("guest")).limit(1).collect(Collectors.toList()).get(0);

            log.info("begin to update predict job status of federation {}", fedPredict.getFederationUuid());
            switch (query.getStr("f_status")) {
                case "running":
                    predict.setDuration(TimeUtils.getDurationStrByTimestamp(System.currentTimeMillis() - predict.getStartTimestamp()));
                    predictService.updateById(predict);
                    log.info("job {} is still running", query.getStr("f_job_id"));
                    break;
                case "success":
                    // 标记状态
                    fed.setStatus(3);
                    predict.setStatus(1);

                    // 更新持续时间
                    predict.setDuration(TimeUtils.getDurationStrByTimestamp(query.getLong("f_end_time") - predict.getStartTimestamp()));

                    federationMapper.save(fed);
                    predictService.updateById(predict);
                    log.info("job {} is success", query.getStr("f_job_id"));
                    break;
                case "failed":
                    // 标记状态
                    fed.setStatus(4);
                    predict.setStatus(2);

                    // 失败时可能没有结束时间，因此不作更新
                    log.info("job {} is failed", query.getStr("f_job_id"));
                    break;
                default:
                    log.warn("there is an unknown status {}", query.getStr("f_status"));
                    break;
            }
        }
    }
}
