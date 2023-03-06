package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.document.FacilityDoc;
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
public class FacilityDocResponse {

	private Long id;

	private String name;

	private String sido;

	private String gugun;

	private String address;

	private float score;

	public static FacilityDocResponse from(SearchHit<FacilityDoc> searchHit) {
		FacilityDoc facilityDoc = searchHit.getContent();
		return FacilityDocResponse.builder()
			.id(facilityDoc.getId())
			.name(facilityDoc.getName())
			.sido(facilityDoc.getSido())
			.gugun(facilityDoc.getGugun())
			.address(facilityDoc.getAddress())
			.score(searchHit.getScore())
			.build();
	}

}
