package com.cmcc.algo.task;

import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.config.CommonConfig;
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

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Component
@Order(value = 1)
@Slf4j
public class SynchronizeDatasetTask implements ApplicationRunner {
    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    CommonConfig commonConfig;

    @Value("${time-interval}")
    private Integer interval;

    @Bean
    public static ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        log.info("new ThreadPoolTaskScheduler in dataset task");
        return new ThreadPoolTaskScheduler();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("start to sync dataset");
        synchronizeDataset();
    }

    private void synchronizeDataset() {
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);

        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("pyton path is {}", commonConfig.getPythonPath());
                    throw new APIException("nothing");
                } catch (Exception e){
                    log.warn("synchronize is failed, the error detail is {}", e.getMessage());
                }
            }
        }, new CronTrigger("0 */"+ interval +" * * * *"));
    }

}
