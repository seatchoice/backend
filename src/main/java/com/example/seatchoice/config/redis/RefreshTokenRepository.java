package com.example.seatchoice.config.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
