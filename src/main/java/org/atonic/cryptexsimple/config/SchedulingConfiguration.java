package org.atonic.cryptexsimple.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulingConfiguration {
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("scheduled-task-");
        taskScheduler.setErrorHandler(t -> log.error("Scheduled task failed. Error: {0}", t));
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);

        return taskScheduler;
    }
}
