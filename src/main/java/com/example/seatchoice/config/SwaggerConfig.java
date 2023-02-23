package com.example.seatchoice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@OpenAPIDefinition(
	info = @Info(
		title = "자리어때 ?",
		version = "1.0",
		description = "자리어때 사이트 API 입니다.",
		contact = @Contact(
			name = "zeropepsi",
			url = "https://github.com/seatchoice"
		)
	),
	servers = @Server(url = "https://seatchoice.site")
)
public class SwaggerConfig {

	private String basePackage = "com.example.seatchoice";
	@Bean
	public GroupedOpenApi api() {
		String[] paths = {"/api/**"};
		String[] excludePaths = {"/api/**/save"};

		return GroupedOpenApi.builder()
			.group("seatchoice-api")
			.packagesToScan(basePackage)
			.pathsToMatch(paths)
			.pathsToExclude(excludePaths)
			.build();
	}

}
