package com.example.seatchoice.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.service.SchedulerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class SchedulerConfigTest {

	@Mock
	private JobLauncher jobLauncher;
	@Mock
	private BatchConfig batchConfig;
	@Mock
	private SchedulerService schedulerService;
	@InjectMocks
	private SchedulerConfig schedulerConfig;

	@Test
	void updatePerformance()
		throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		// given
		JobExecution jobExecution = new JobExecution(1L);
		jobExecution.setStatus(BatchStatus.COMPLETED);
		given(jobLauncher.run(eq(batchConfig.job()), any(JobParameters.class)))
			.willReturn(jobExecution);

		// when
		schedulerConfig.updatePerformance();

		// then
		verify(jobLauncher).run(eq(batchConfig.job()), any(JobParameters.class));
	}

	@Test
	void performanceCompleteMySql() {
		// given
		doNothing()
			.when(schedulerService).deletePerformanceCompleteMysql();

		// then
		schedulerConfig.deletePerformanceCompleteMySql();

		// when
		verify(schedulerService, times(1)).deletePerformanceCompleteMysql();
	}

	@Test
	void performanceCompleteEs() {
		// given
		doNothing()
			.when(schedulerService).deletePerformanceCompleteEs();

		// then
		schedulerConfig.deletePerformanceCompleteEs();

		// when
		verify(schedulerService, times(1)).deletePerformanceCompleteEs();

	}
}