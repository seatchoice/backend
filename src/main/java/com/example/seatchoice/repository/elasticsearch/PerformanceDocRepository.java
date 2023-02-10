package com.example.seatchoice.repository.elasticsearch;

import com.example.seatchoice.entity.document.PerformanceDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceDocRepository extends ElasticsearchRepository<PerformanceDoc, Long> {

}