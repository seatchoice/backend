package com.example.seatchoice.service;

import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.repository.PerformanceRepository;
import java.time.LocalDate;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SchedulerService {

	private final ElasticsearchOperations elasticsearchOperations;

	private final PerformanceRepository performanceRepository;

	/**
	 * 기간 지난 데이터 체크 후 삭제 - rds(mysql)
	 */
	@Transactional
	public void deletePerformanceCompleteMysql() {
		performanceRepository.deleteByEndDate(LocalDate.now());
	}

	/**
	 * 기간 지난 데이터 체크 후 삭제 - elasticsearch
	 */
	@Transactional
	public void deletePerformanceCompleteEs() {
		NativeSearchQuery query = new NativeSearchQueryBuilder()
			.withQuery(QueryBuilders.boolQuery()
				.must(QueryBuilders.rangeQuery("endDate").lt(new Date())))
			.build();

		elasticsearchOperations.delete(query, PerformanceDoc.class);
	}

}
