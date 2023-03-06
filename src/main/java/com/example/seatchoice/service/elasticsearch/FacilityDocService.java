package com.example.seatchoice.service.elasticsearch;

import com.example.seatchoice.dto.response.FacilityDocResponse;
import com.example.seatchoice.entity.Facility;
import com.example.seatchoice.entity.document.FacilityDoc;
import com.example.seatchoice.repository.FacilityRepository;
import com.example.seatchoice.repository.elasticsearch.FacilityDocRepository;
import com.example.seatchoice.util.QueryParsingUtil;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.Operator;
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
public class FacilityDocService {

	private final ElasticsearchOperations elasticsearchOperations;
	private final FacilityRepository facilityRepository;
	private final FacilityDocRepository facilityDocRepository;

	/**
	 * mysql에 저장된 facility es에 저장 (초기세팅)
	 */
	public void saveFacilities() {
		List<Facility> facilityList = facilityRepository.findAll();

		facilityDocRepository.saveAll(facilityList.stream()
			.map(FacilityDoc::from)
			.collect(Collectors.toList()));
	}

	/**
	 * 시설 검색
	 */
	public List<FacilityDocResponse> searchFacility(
		String name, Float score, Long after, int size, String sido, String gugun) {

		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
		if (score != null && after != null) {
			queryBuilder.withSearchAfter(List.of(score, after));
		}

		NativeSearchQuery searchQuery = queryBuilder
			.withQuery(QueryBuilders.boolQuery()
				.should(QueryBuilders.queryStringQuery("*" + QueryParsingUtil.escape(name) + "*").field("name"))
				.should(QueryBuilders.matchQuery("name", name).operator(Operator.AND))
				.must(sido == null ? QueryBuilders.matchAllQuery()
					: QueryBuilders.termQuery("sido", sido))
				.must(gugun == null ? QueryBuilders.matchAllQuery()
					: QueryBuilders.termQuery("gugun", gugun)))
			.withPageable(PageRequest.of(0, size))
			.withSorts(
				SortBuilders.scoreSort().order(SortOrder.DESC),
				SortBuilders.fieldSort("id").order(SortOrder.ASC)
			)
			.build();

		SearchHits<FacilityDoc> searchHits = elasticsearchOperations.search(searchQuery, FacilityDoc.class);

		return searchHits.stream()
			.map(FacilityDocResponse::from)
			.collect(Collectors.toList());
	}
}