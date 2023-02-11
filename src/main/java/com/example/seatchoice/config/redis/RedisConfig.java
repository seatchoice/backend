package com.example.seatchoice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		redisStandaloneConfiguration.setPassword(password);

		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<Long, Object> redisTemplate(){
		RedisTemplate<Long,Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		return redisTemplate;
	}
}
