package com.example.seatchoice.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.repository.PerformanceRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

	@Mock
	private PerformanceRepository performanceRepository;
	@Mock
	private ElasticsearchOperations elasticsearchOperations;
	@InjectMocks
	SchedulerService schedulerService;

	@Test
	@DisplayName("기간지난 공연 데이터 모두 삭제 - rds(mysql)")
	void deletePerformanceCompleteMysql() {

		schedulerService.deletePerformanceCompleteMysql();

		verify(performanceRepository, times(1)).deleteByEndDate(LocalDate.now());

	}

}