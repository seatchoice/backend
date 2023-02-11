package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.INVALID_TOKEN;
import static com.example.seatchoice.type.ErrorCode.NOT_EXPIRED_TOKEN;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;

import com.example.seatchoice.config.AppProperties;
import com.example.seatchoice.config.jwt.AuthToken;
import com.example.seatchoice.config.jwt.AuthTokenProvider;
import com.example.seatchoice.config.jwt.UserPrincipal;
import com.example.seatchoice.config.redis.RefreshToken;
import com.example.seatchoice.config.redis.RefreshTokenRepository;
import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.dto.auth.UserInfo;
import com.example.seatchoice.dto.param.AuthParam;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.type.MemberRole;
import com.example.seatchoice.util.CookieUtil;
import com.example.seatchoice.util.HeaderUtil;
import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

    public Token login(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthParam authParam
    ) {

        Member member = memberRepository.findByOauthId(authParam.getId());

        if (member == null) {
            throw new CustomException(NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND);
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(member.getRole().getCode()));
        UserPrincipal principal = UserPrincipal.create(member);
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(principal, null, roles);

        String userId = authParam.getId();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
            userId,
            ((UserPrincipal) authentication.getPrincipal()).getMemberRole().getCode(),
            new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        AuthToken refreshToken = tokenProvider.createAuthToken(
            appProperties.getAuth().getTokenSecret(),
            new Date(now.getTime() + refreshTokenExpiry)
        );

        // userId refresh token 으로 DB 확인
        Long memberId = Long.parseLong(userId);
        Optional<RefreshToken> redisRefreshToken = refreshTokenRepository.findById(memberId);
        if (redisRefreshToken.isEmpty()) {
            // 없는 경우 새로 등록
            RefreshToken newRefreshToken = new RefreshToken(memberId, refreshToken.getToken());
            refreshTokenRepository.save(newRefreshToken);
        } else {
            // DB에 refresh 토큰 업데이트
            RefreshToken updateRefreshToken = redisRefreshToken.get();
            updateRefreshToken.setMemberId(memberId);
            updateRefreshToken.setRefreshToken(refreshToken.getToken());
            refreshTokenRepository.save(updateRefreshToken);
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        return new Token(accessToken.getToken());
    }

    public Token refreshToken (HttpServletRequest request, HttpServletResponse response) {
        // access token 확인
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
        if (!authToken.validate()) {
            throw new CustomException(INVALID_TOKEN, HttpStatus.BAD_REQUEST);
        }

        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            throw new CustomException(NOT_EXPIRED_TOKEN, HttpStatus.BAD_REQUEST);
        }

        String userId = claims.getSubject();
        MemberRole memberRole = MemberRole.of(claims.get("role", String.class));

        // refresh token
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
            .map(Cookie::getValue)
            .orElse((null));
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

        if (authRefreshToken.validate()) {
            throw new CustomException(INVALID_TOKEN, HttpStatus.BAD_REQUEST);
        }

        // userId refresh token 으로 DB 확인
        Long memberId = Long.parseLong(userId);
        RefreshToken redisRefreshToken = refreshTokenRepository.findById(memberId).orElseThrow();

        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
            userId,
            memberRole.getCode(),
            new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            authRefreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
            );

            // DB에 refresh 토큰 업데이트
            redisRefreshToken.setRefreshToken(authRefreshToken.getToken());
            refreshTokenRepository.save(redisRefreshToken);

            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
        }

        return new Token(newAccessToken.getToken());
    }
}
