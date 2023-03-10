package com.example.seatchoice.service.elasticsearch;

import com.example.seatchoice.dto.response.PerformanceDocResponse;
import com.example.seatchoice.entity.Performance;
import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.repository.PerformanceRepository;
import com.example.seatchoice.repository.elasticsearch.PerformanceDocRepository;
import com.example.seatchoice.util.QueryParsingUtil;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
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
	public List<PerformanceDocResponse> searchPerformance(
		String name, Float score, Long after, int size, Date startDate, Date endDate) {

		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
		if (score != null && after != null) {
			queryBuilder.withSearchAfter(List.of(score, after));
		}

		QueryBuilder dateRangeQuery;
		if (startDate != null && endDate != null) {
			dateRangeQuery = QueryBuilders.boolQuery()
				.mustNot(QueryBuilders.rangeQuery("startDate").gt(endDate))
				.mustNot(QueryBuilders.rangeQuery("endDate").lt(startDate));
		} else {
			dateRangeQuery = QueryBuilders.matchAllQuery();
		}

		NativeSearchQuery searchQuery = queryBuilder
			.withQuery(QueryBuilders.boolQuery()
				.should(QueryBuilders.queryStringQuery("*" + QueryParsingUtil.escape(name) + "*").field("name"))
				.should(QueryBuilders.matchQuery("name", name).operator(Operator.AND))
				.must(dateRangeQuery))
			.withSorts(
				SortBuilders.scoreSort().order(SortOrder.DESC),
				SortBuilders.fieldSort("id").order(SortOrder.ASC)
			)
			.withPageable(PageRequest.of(0, size))
			.build();

		SearchHits<PerformanceDoc> searchHits = elasticsearchOperations.search(searchQuery, PerformanceDoc.class);
		return searchHits.stream()
			.map(PerformanceDocResponse::from)
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