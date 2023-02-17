package com.example.seatchoice.service.elasticsearch;

import com.example.seatchoice.entity.Performance;
import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.repository.PerformanceRepository;
import com.example.seatchoice.repository.elasticsearch.PerformanceDocRepository;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceDocService {

	private final ElasticsearchOperations elasticsearchOperations;
	private final PerformanceRepository performanceRepository;
	private final PerformanceDocRepository performanceDocRepository;

	/**
	 * 공연 검색
	 */
	public List<PerformanceDoc> searchPerformance(
		String name, Long after, int size, Date startDate, Date endDate) {

		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
		if (after != null) {
			queryBuilder.withSearchAfter(List.of(after));
		}

		if (startDate != null && endDate != null) {
			queryBuilder.withFilter(QueryBuilders.boolQuery()
				.should(QueryBuilders.rangeQuery("startDate").lte(endDate))
				.should(QueryBuilders.rangeQuery("endDate").gte(startDate)));
		}

		NativeSearchQuery searchQuery = queryBuilder
			.withQuery(QueryBuilders.queryStringQuery("*" + name + "*").field("name"))
			.withSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
			.withPageable(PageRequest.of(0, size))
			.build();

		SearchHits<PerformanceDoc> searchHits = elasticsearchOperations.search(searchQuery, PerformanceDoc.class);
		return searchHits.stream()
			.map(SearchHit::getContent)
			.collect(Collectors.toList());

	}

	/**
	 * mysql에 저장된 performance es에 저장 (초기 세팅)
	 */
	public void savePerformances() {
		List<Performance> performanceList = performanceRepository.findAll();

		performanceDocRepository.saveAll(performanceList.stream()
			.map(PerformanceDoc::from)
			.collect(Collectors.toList()));
	}

}