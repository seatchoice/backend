package com.example.seatchoice.config;

import com.example.seatchoice.config.jwt.AuthTokenProvider;
import com.example.seatchoice.config.jwt.CustomUserDetailsService;
import com.example.seatchoice.config.jwt.RestAuthenticationEntryPoint;
import com.example.seatchoice.config.jwt.TokenAccessDeniedHandler;
import com.example.seatchoice.config.jwt.TokenAuthenticationFilter;
import com.example.seatchoice.config.oauth.CustomOAuth2UserService;
import com.example.seatchoice.config.oauth.OAuth2AuthenticationFailureHandler;
import com.example.seatchoice.config.oauth.OAuth2AuthenticationSuccessHandler;
import com.example.seatchoice.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.seatchoice.config.redis.RefreshTokenRepository;
import com.example.seatchoice.type.MemberRole;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CorsProperties corsProperties;
	private final AppProperties appProperties;
	private final AuthTokenProvider tokenProvider;
	private final CustomUserDetailsService userDetailsService;
	private final CustomOAuth2UserService oAuth2UserService;
	private final TokenAccessDeniedHandler tokenAccessDeniedHandler;

	private final RefreshTokenRepository refreshTokenRepository;

	/*
	 * UserDetailsService 설정
	 * */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors()
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.exceptionHandling()
			.authenticationEntryPoint(new RestAuthenticationEntryPoint())
			.accessDeniedHandler(tokenAccessDeniedHandler)
			.and()

			.authorizeRequests()
			.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			.antMatchers("/api/**").hasAnyAuthority(MemberRole.USER.getCode())
			.antMatchers("/api/**/admin/**").hasAnyAuthority(MemberRole.ADMIN.getCode())
			.anyRequest().authenticated()
			
			.and()
			.oauth2Login()
			.authorizationEndpoint()
			.baseUri("/oauth2/authorization")
			.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
			.and()
			.redirectionEndpoint()
			.baseUri("/*/oauth2/code/*")
			.and()
			.userInfoEndpoint()
			.userService(oAuth2UserService)
			.and()
			.successHandler(oAuth2AuthenticationSuccessHandler())
			.failureHandler(oAuth2AuthenticationFailureHandler());

		http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	/*
	 * auth 매니저 설정
	 * */
	@Override
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	/*
	 * security 설정 시, 사용할 인코더 설정
	 * */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * 토큰 필터 설정
	 * */
	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(tokenProvider);
	}

	/*
	 * 쿠키 기반 인가 Repository
	 * 인가 응답을 연계 하고 검증할 때 사용.
	 * */
	@Bean
	public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
		return new OAuth2AuthorizationRequestBasedOnCookieRepository();
	}

	/*
	 * Oauth 인증 성공 핸들러
	 * */
	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler(
			tokenProvider,
			appProperties,
			refreshTokenRepository,
			oAuth2AuthorizationRequestBasedOnCookieRepository()
		);
	}

	/*
	 * Oauth 인증 실패 핸들러
	 * */
	@Bean
	public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
		return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
	}

	/*
	 * Cors 설정
	 * */
	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
		corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
		corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
		corsConfig.setAllowCredentials(true);
		corsConfig.setMaxAge(corsConfig.getMaxAge());

		corsConfigSource.registerCorsConfiguration("/**", corsConfig);
		return corsConfigSource;
	}
}
