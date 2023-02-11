package com.example.seatchoice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 87840)
@Setter
@Getter
public class RefreshToken {

    @Id
    private Long memberId;
    private String refreshToken;

    public RefreshToken(final Long memberId, final String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getMemberId() {
        return memberId;
    }
}
