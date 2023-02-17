package com.example.seatchoice.client.kopis;

import java.time.LocalDate;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@XmlRootElement(name = "dbs")
public class PerformanceResponse {

	private List<PerformanceVo> performanceVoList;

	@XmlElement(name="db")
	public List<PerformanceVo> getPerformanceVoList() {
		return performanceVoList;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@XmlRootElement(name = "db")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PerformanceVo {

		@XmlElement(name = "mt20id")
		private String mt20id; // 공연ID

		@XmlElement(name = "prfnm")
		private String prfnm; // 공연명

		@XmlElement(name = "prfpdfrom")
		private String prfpdfrom; // 공연시작일

		@XmlElement(name = "prfpdto")
		private String prfpdto; // 공연종료일

		@XmlElement(name = "fcltynm")
		private String fcltynm; // 공연시설명(공연장명)

		@XmlElement(name = "poster")
		private String poster; // 포스터이미지경로

		@XmlElement(name = "genrenm")
		private String genrenm; // 공연 장르명

		@XmlElement(name = "prfstate")
		private String prfstate; // 공연상태

		@XmlElement(name = "openrun")
		private String openrun; // 오픈런

		public LocalDate getPrfpdfromDate() {
			return LocalDate.of(Integer.parseInt(this.prfpdfrom.substring(0, 4)),
				Integer.parseInt(this.prfpdfrom.substring(5, 7)),
				Integer.parseInt(this.prfpdfrom.substring(8))
			);
		}

		public LocalDate getPrfpdtoDate() {
			return LocalDate.of(Integer.parseInt(this.prfpdto.substring(0, 4)),
				Integer.parseInt(this.prfpdto.substring(5, 7)),
				Integer.parseInt(this.prfpdto.substring(8))
			);
		}

	}

}
