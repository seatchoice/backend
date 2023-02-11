package com.example.seatchoice.config;

import com.example.seatchoice.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

	private final SchedulerService schedulerService;

	@Order(1)
	@Scheduled(cron = "0 0 0 * * ?")
	public void PerformanceCompleteMySql() {
		schedulerService.deletePerformanceCompleteMysql();
	}

	@Order(2)
	@Scheduled(cron = "0 0 0 * * ?")
	public void PerformanceCompleteEs() {
		schedulerService.deletePerformanceCompleteEs();
	}

}
