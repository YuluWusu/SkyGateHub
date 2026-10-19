package com.example.skygatehub.common.concurrent;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Cấu hình ThreadPoolTaskScheduler cho tác vụ định kỳ @Scheduled (Tiêu chí 5.1).
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("SkyGate-Sched-");
        scheduler.initialize();
        return scheduler;
    }
}
