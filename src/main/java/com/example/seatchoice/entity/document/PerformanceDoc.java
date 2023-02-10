package com.example.seatchoice.entity.document;

import com.example.seatchoice.entity.Performance;
import java.time.ZoneOffset;
import java.util.Date;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "performance")
public class PerformanceDoc {

	@Id
	@Field(type = FieldType.Keyword)
	private Long id;

	@Field(type = FieldType.Text)
	private String name;

	@Field(type = FieldType.Date)
	private Date startDate;

	@Field(type = FieldType.Date)
	private Date endDate;

	@Field(type = FieldType.Text)
	private String poster;

	@Field(type = FieldType.Keyword)
	private String genrenm;

	@Field(type = FieldType.Boolean)
	private Boolean openrun;

	public static PerformanceDoc from(Performance p) {
		return PerformanceDoc.builder()
			.id(p.getId())
			.name(p.getPrfnm())
			.startDate(Date.from(p.getPrfpdfrom().atStartOfDay().toInstant(ZoneOffset.UTC)))
			.endDate(Date.from(p.getPrfpdto().atStartOfDay().toInstant(ZoneOffset.UTC)))
			.poster(p.getPoster())
			.genrenm(p.getGenrenm())
			.openrun(p.isOpenrun())
			.build();
	}

}