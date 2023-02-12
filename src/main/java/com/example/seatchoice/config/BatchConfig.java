package com.example.seatchoice.config;

import com.example.seatchoice.client.kopis.PerformanceResponse.Prf;
import com.example.seatchoice.config.batch.DataShareBean;
import com.example.seatchoice.config.batch.PerformanceItemWriter;
import com.example.seatchoice.config.batch.PrfItemReader;
import com.example.seatchoice.config.batch.PrfProcessor;
import com.example.seatchoice.entity.Performance;
import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.repository.PerformanceRepository;
import com.example.seatchoice.repository.TheaterRepository;
import com.example.seatchoice.repository.elasticsearch.PerformanceDocRepository;
import com.example.seatchoice.service.KopisService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataShareBean<Performance> dataShareBean;

	private final KopisService kopisService;
	private final TheaterRepository theaterRepository;
	private final PerformanceRepository performanceRepository;
	private final PerformanceDocRepository performanceDocRepository;

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
			.start(step1(reader(), processor(), writer()))
			.next(step2())
			.build();
	}

	@Bean
	@JobScope
	public Step step1(PrfItemReader reader, PrfProcessor processor, PerformanceItemWriter writer) {
		return stepBuilderFactory.get("step1")
			.<Prf, Performance>chunk(10)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.build();
	}

	@Bean
	@JobScope
	public Step step2() {
		return stepBuilderFactory.get("step2")
			.tasklet((contribution, chunkContext) -> {
				List<PerformanceDoc> list = dataShareBean.getData("PERFORMANCE")
					.stream().map(PerformanceDoc::from).collect(Collectors.toList());
				performanceDocRepository.saveAll(list);
				return RepeatStatus.FINISHED;
			}).build();
	}

	@Bean
	@StepScope
	public PrfItemReader reader() {
		return new PrfItemReader(kopisService);
	}

	@Bean
	@StepScope
	public PrfProcessor processor() {
		return new PrfProcessor(dataShareBean, kopisService, performanceRepository, theaterRepository);
	}

	@Bean
	@StepScope
	public PerformanceItemWriter writer() {
		return new PerformanceItemWriter(performanceRepository);
	}

}
