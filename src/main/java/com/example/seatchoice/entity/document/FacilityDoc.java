package com.example.seatchoice.entity.document;

import com.example.seatchoice.entity.Facility;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "facility")
public class FacilityDoc {

	@Id
	private Long id;

	private String name;

	private Integer totalSeatCnt;

	private String sido;

	private String gugun;

	private String address;

	public static FacilityDoc from(Facility f) {
		return FacilityDoc.builder()
			.id(f.getId())
			.name(f.getName())
			.sido(f.getSido())
			.gugun(f.getGugun())
			.address(f.getAddress())
			.build();
	}

}