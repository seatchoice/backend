package com.example.seatchoice.batch;

import com.example.seatchoice.client.kopis.PerformanceResponse.PerformanceVo;
import com.example.seatchoice.entity.Performance;
import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.repository.PerformanceRepository;
import com.example.seatchoice.repository.TheaterRepository;
import com.example.seatchoice.repository.elasticsearch.PerformanceDocRepository;
import com.example.seatchoice.service.KopisService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	private final KopisService kopisService;
	private final TheaterRepository theaterRepository;
	private final PerformanceRepository performanceRepository;
	private final PerformanceDocRepository performanceDocRepository;


	@Bean
	public Job newPerformanceJob() {
		return jobBuilderFactory.get("newPerformanceJob")
			.start(step1())
			.next(step2())
			.build();
	}

	@Bean
	@JobScope
	public Step step1() {
		return stepBuilderFactory.get("step1")
			.<PerformanceVo, Performance>chunk(10)
			.reader(reader1())
			.processor(processor1())
			.writer(writer1())
			.build();
	}

	@Bean
	@JobScope
	public Step step2() {
		return stepBuilderFactory.get("step2")
			.<Performance, PerformanceDoc>chunk(10)
			.reader(reader2())
			.processor(processor2())
			.writer(writer2())
			.build();
	}

	@Bean
	@StepScope
	public PerformanceVoItemReader reader1() {
		return new PerformanceVoItemReader(kopisService);
	}

	@Bean
	@StepScope
	public PerformanceVoProcessor processor1() {
		return new PerformanceVoProcessor(kopisService, performanceRepository, theaterRepository);
	}

	@Bean
	@StepScope
	public RepositoryItemWriter<Performance> writer1() {
		return new RepositoryItemWriterBuilder<Performance>()
			.repository(performanceRepository)
			.build();
	}

	@Bean
	@StepScope
	public RepositoryItemReader<Performance> reader2() {
		return new RepositoryItemReaderBuilder<Performance>()
			.repository(performanceRepository)
			.methodName("findByCreatedAt")
			.arguments(LocalDate.now())
			.pageSize(10)
			.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<Performance, PerformanceDoc> processor2() {
		return PerformanceDoc::from;
	}

	@Bean
	@StepScope
	public PerformanceDocItemWriter writer2() {
		return new PerformanceDocItemWriter(performanceDocRepository);
	}

}
