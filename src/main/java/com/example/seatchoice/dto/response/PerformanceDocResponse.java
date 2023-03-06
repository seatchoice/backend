package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.document.PerformanceDoc;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.core.SearchHit;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PerformanceDocResponse {

	private Long id;

	private String name;

	private Date startDate;

	private Date endDate;

	private String poster;

	private String genrenm;

	private Boolean openrun;
	private float score;

	public static PerformanceDocResponse from(SearchHit<PerformanceDoc> searchHit) {

		PerformanceDoc performanceDoc = searchHit.getContent();
		return PerformanceDocResponse.builder()
			.id(performanceDoc.getId())
			.name(performanceDoc.getName())
			.startDate(performanceDoc.getStartDate())
			.endDate(performanceDoc.getEndDate())
			.poster(performanceDoc.getPoster())
			.genrenm(performanceDoc.getGenrenm())
			.openrun(performanceDoc.getOpenrun())
			.score(searchHit.getScore())
			.build();
	}

}
