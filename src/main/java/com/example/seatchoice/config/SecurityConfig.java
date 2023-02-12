package com.example.seatchoice.config;

import com.example.seatchoice.config.jwt.JwtAuthenticationFilter;
import com.example.seatchoice.config.jwt.TokenService;
import com.example.seatchoice.config.oauth.CustomOAuth2UserService;
import com.example.seatchoice.config.oauth.OAuth2SuccessHandler;
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

	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final TokenService tokenService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.formLogin().disable()
			.authorizeRequests()
			.antMatchers(
				"/",
				"/api/**"
			).permitAll()
			.and()
			.oauth2Login()
			.successHandler(oAuth2SuccessHandler)
			.userInfoEndpoint().userService(customOAuth2UserService);

		http.addFilterBefore(new JwtAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
