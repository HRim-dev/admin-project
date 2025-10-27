package com.hrimDev.jwt.service;


import com.hrimDev.common.util.AESUtil;
import io.jsonwebtoken.*;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private static final String CLAIM_KEY = "ASSET";

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.token-validity-in-seconds}")
    private long expiredMs;

    private final AESUtil aesUtil;


    /**
     * 초기 토큰 생성
     * @param authentication
     * @return
     */
    public String createAccessToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Claims claims = Jwts.claims().setSubject(principal.getUsername());
        claims.put(CLAIM_KEY, aesUtil.encrypt("{\"id\":" + principal.getUsername() +"}"));
        claims.put("AUTH_ROLE", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        String token = this.createToken(claims, expiredMs);
        return aesUtil.encrypt(token);
    }

    /**
     * 토큰 갱신
     * @param encryptedToken
     * @return
     */
    public String refreshAccessToken(String encryptedToken) {
        Claims claims = validateToken(encryptedToken);
        return aesUtil.encrypt(this.createToken(claims, expiredMs));
    }

    /**
     * 토큰 검증
     * @param encryptedToken
     * @return
     */
    public Authentication getAuthentication(String encryptedToken) {
        //토큰 복호화
        Claims claims = validateToken(encryptedToken);
        if(!aesUtil.decrypt(claims.get(CLAIM_KEY).toString()).startsWith("{")) {
            throw new JwtException("로그인 인증 정보가 유효하지 않습니다. \n 다시 로그인 하여주세요.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("AUTH_ROLE").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, claims, authorities);
    }

    /**
     * 특정 Claim 조회
     * @param token
     * @param claimName
     * @return
     */
    private String extractClaim(String token, String claimName) {
        final Claims claims = this.extractAllClaims(token);
        return claims.get(claimName, String.class);
    }

    /**
     * JWT의 모든 Claim 조회
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new JwtException("로그인 인증 정보가 유효하지 않습니다. \n 다시 로그인 하여주세요.");
        }
    }

    public Claims validateToken(String encryptedToken) {
        try {
            // 토큰 복호화
            String token = aesUtil.decrypt(encryptedToken);
            return extractAllClaims(token);
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature. 잘못된 JWT 시그니처");
            throw new JwtException("로그인 인증 정보가 유효하지 않습니다. \n 다시 로그인 하여주세요.");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token.");
            throw new JwtException("로그인 인증 정보가 유효하지 않습니다. \n 다시 로그인 하여주세요.");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token.");
            throw new ExpiredJwtException(null, e.getClaims(), "로그인 기한이 만료 되었습니다. \n 다시 로그인 하여주세요.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid.");
            throw new JwtException("로그인 인증 정보가 유효하지 않습니다. \n 다시 로그인 하여주세요.");
        }
    }

    /**
     * 토큰 생성
     * @param claims
     * @param validitySeconds
     * @return
     */
    public String createToken(Claims claims, long validitySeconds) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validitySeconds))
                .signWith(getSigningKey(), signatureAlgorithm)
                .compact();
    }

    /**
     * SecretKey 형태의 Sing key 생성
     * @return
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
