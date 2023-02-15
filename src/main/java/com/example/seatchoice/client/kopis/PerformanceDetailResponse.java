package com.example.seatchoice.client.kopis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "dbs")
public class PerformanceDetailResponse {
	private PrfDetail prfDetail;

	@XmlElement(name="db")
	public PrfDetail getPrfDetail() {
		return prfDetail;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@XmlRootElement(name = "db")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PrfDetail {

		@XmlElement(name = "mt20id")
		private String mt20id; // 공연ID

		@XmlElement(name = "fcltynm")
		private String fcltynm; // 공연시설명(공연장명)

	}

}
