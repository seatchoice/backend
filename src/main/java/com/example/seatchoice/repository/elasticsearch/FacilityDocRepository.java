package com.example.seatchoice.repository.elasticsearch;

import com.example.seatchoice.entity.document.FacilityDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityDocRepository extends ElasticsearchRepository<FacilityDoc, Long> {

}
