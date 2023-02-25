package com.example.seatchoice.config;

import com.example.seatchoice.batch.BatchConfig;
import com.example.seatchoice.service.PerformanceService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

	private final JobLauncher jobLauncher;
	private final BatchConfig batchConfig;
	private final PerformanceService performanceService;

//	@Order(3)
//	@Scheduled(cron = "0 0 0 1 * ?") // 매달 1일 오전 12시마다 업데이트가 진행됩니다.
	public void updatePerformance() {
		Map<String, JobParameter> map = new HashMap<>();

		String time = getDateFormatString();
		map.put("time", new JobParameter(time));
		JobParameters jobParameters = new JobParameters(map);

		try {
			JobExecution jobExecution =
				jobLauncher.run(batchConfig.newPerformanceJob(), jobParameters);

			log.info("Job Execution: " + jobExecution.getStatus());
			log.info("Job getJobConfigurationName: " + jobExecution.getJobConfigurationName());
			log.info("Job getJobId: " + jobExecution.getJobId());
			log.info("Job getExitStatus: " + jobExecution.getExitStatus());
			log.info("Job getJobInstance: " + jobExecution.getJobInstance());
			log.info("Job getStepExecutions: " + jobExecution.getStepExecutions());
			log.info("Job getLastUpdated: " + jobExecution.getLastUpdated());
			log.info("Job getFailureExceptions: " + jobExecution.getFailureExceptions());

		} catch (JobExecutionAlreadyRunningException |
				 JobInstanceAlreadyCompleteException |
				 JobParametersInvalidException | JobRestartException e) {

			log.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Order(1)
//	@Scheduled(cron = "0 0 0 * * ?") // 매일 오전 12시마다 삭제가 진행됩니다.
	public void deletePerformanceCompleteMySql() {
		performanceService.deletePerformanceCompleteMysql();
	}

//	@Order(2)
//	@Scheduled(cron = "0 0 0 * * ?") // 매일 오전 12시마다 삭제가 진행됩니다.
	public void deletePerformanceCompleteEs() {
		performanceService.deletePerformanceCompleteEs();
	}


	private static String getDateFormatString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

		return format.format(new Date());
	}

}
