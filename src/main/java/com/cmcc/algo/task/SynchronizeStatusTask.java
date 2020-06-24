package com.cmcc.algo.task;

import com.cmcc.algo.service.ITrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
@Order(value = 1)
public class SynchronizeStatusTask implements ApplicationRunner {
    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    ITrainService trainService;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        synchronizeTaskStatus();
    }

    private void synchronizeTaskStatus(){
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);

        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                // TODO 发送请求检测任务（训练和预测是否为两个请求？）成功或失败，修改数据库中任务状态和联邦状态
            }
        }, new CronTrigger("0 */1 * * * *"));
    }
}
