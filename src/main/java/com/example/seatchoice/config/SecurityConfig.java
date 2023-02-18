package com.example.seatchoice.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.example.seatchoice.service.auth.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	private final TokenService tokenService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.formLogin().disable()
			.authorizeRequests()
			.antMatchers(
				POST,
				"/api/oauth/{provider}/login"
			).permitAll()
			.antMatchers(
				GET,
				"/api/theaters/**",
				"/api/search",
				"/api/reviews",
				"/api/reviews/**"
			).permitAll()
			// 유저 권한이 필요한 api 추가
			.antMatchers(
				"/api/**",
				"/exchange/**",
				"/pub/**"
			).hasRole("USER");

		http.addFilterBefore(new JwtAuthenticationFilter(tokenService),
			UsernamePasswordAuthenticationFilter.class);
		http.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());

		return http.build();
	}
}
