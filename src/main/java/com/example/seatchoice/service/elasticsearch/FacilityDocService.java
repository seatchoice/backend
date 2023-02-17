package com.example.seatchoice.service.elasticsearch;

import com.example.seatchoice.entity.Facility;
import com.example.seatchoice.entity.document.FacilityDoc;
import com.example.seatchoice.repository.FacilityRepository;
import com.example.seatchoice.repository.elasticsearch.FacilityDocRepository;
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
public class FacilityDocService {

	private final ElasticsearchOperations elasticsearchOperations;
	private final FacilityRepository facilityRepository;
	private final FacilityDocRepository facilityDocRepository;

	/**
	 * 시설 검색
	 */
	public List<FacilityDoc> searchFacility(
		String name, Long after, int size, String sido, String gugun) {

		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
		if (after != null) {
			queryBuilder.withSearchAfter(List.of(after));
		}

		NativeSearchQuery searchQuery = queryBuilder
			.withQuery(QueryBuilders.boolQuery()
				.must(QueryBuilders.queryStringQuery("*" + name + "*").field("name"))
				.must(sido == null ? QueryBuilders.matchAllQuery() : QueryBuilders.termQuery("sido", sido))
				.must(gugun == null ? QueryBuilders.matchAllQuery() : QueryBuilders.termQuery("gugun", gugun)))
			.withSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
			.withPageable(PageRequest.of(0, size))
			.build();


		SearchHits<FacilityDoc> searchHits = elasticsearchOperations.search(searchQuery, FacilityDoc.class);
		return searchHits.stream()
			.map(SearchHit::getContent)
			.collect(Collectors.toList());
	}

	/**
	 * mysql에 저장된 facility es에 저장 (초기세팅)
	 */
	public void saveFacilities() {
		List<Facility> facilityList = facilityRepository.findAll();

		facilityDocRepository.saveAll(facilityList.stream()
			.map(FacilityDoc::from)
			.collect(Collectors.toList()));
	}

}
