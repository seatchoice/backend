package com.example.seatchoice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.repository.PerformanceRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

	@Mock
	private PerformanceRepository performanceRepository;
	@Mock
	private ElasticsearchOperations elasticsearchOperations;
	@InjectMocks
	PerformanceService performanceService;

	@Test
	@DisplayName("기간 지난 공연 데이터 모두 삭제 - rds(mysql)")
	void deletePerformanceCompleteMysql() {

		performanceService.deletePerformanceCompleteMysql();

		verify(performanceRepository, times(1)).deleteByEndDate(LocalDate.now());

	}

	@Test
	@DisplayName("기간 지난 공연 데이터 모두 삭제 - elasticsearch")
	void deletePerformanceCompleteEs() {
		// given
		ByQueryResponse responseMock = mock(ByQueryResponse.class);

		given(elasticsearchOperations.delete(any(Query.class), eq(PerformanceDoc.class)))
			.willReturn(responseMock);

		// when
		performanceService.deletePerformanceCompleteEs();

		// then
		verify(elasticsearchOperations, times(1))
			.delete(any(NativeSearchQuery.class), eq(PerformanceDoc.class));

	}

}