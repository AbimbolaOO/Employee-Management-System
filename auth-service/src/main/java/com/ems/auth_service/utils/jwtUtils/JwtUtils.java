package com.ems.auth_service.utils.jwtUtils;

import com.ems.auth_service.dto.jwt.TokenDetailDTO;
import com.ems.auth_service.entity.Auth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Slf4j
@Component
public class JwtUtils {
    //    NOTE:: JWT time are specified in minutes
    @Value("${jwt.accessTokenSecret}")
    private String accessTokenSecret;

    @Value("${jwt.accessTokenTime}")
    private long accessTokenTime;

    @Value("${jwt.refreshTokenSecret}")
    private String refreshTokenSecret;

    @Value("${jwt.refreshTokenTime}")
    private long refreshTokenTime;

    public Map<String, Object> getJwtClaim(String token) {
        Claims claims = this.extractClaims(token, accessTokenSecret);
        Map<String, Object> claimsObj = new HashMap<>();
        claimsObj.put("employeeId", claims.getSubject());
        claimsObj.put("role", claims.get("role", String.class));
        claimsObj.put("jti", claims.get("jti", String.class));
        claimsObj.put("email", claims.get("email", String.class));
        return claimsObj;
    }

    public Map<String, Object> getRefreshJwtClaim(String token) {
        Claims claims = this.extractClaims(token, refreshTokenSecret);
        claims.put("userId", claims.getSubject());
        claims.put("jti", claims.get("jti", String.class));
        return claims;
    }

    private Claims extractClaims(String token, String secretKey) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateRefreshToken(String token) {
        return isTokenValid(token, refreshTokenSecret);
    }

    public Map<String, Object> generateUserClaim(Auth auth) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", auth.getRole());
        claims.put("email", auth.getEmail());
        claims.put("employeeId", auth.getEmployeeId());

        return claims;
    }

    public Map<String, TokenDetailDTO> getCredentials(Map<String, Object> claims, String subject) {
        TokenDetailDTO accessToken = generateAccessToken(claims, subject);
        TokenDetailDTO refreshToken = generateRefreshToken(subject);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    private TokenDetailDTO generateAccessToken(Map<String, Object> claims, String userId) {
        claims.put("jti", UUID.randomUUID());
        long timeInSec = accessTokenTime * 60;

        String token = Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeInSec * 1000))
                .signWith(getSignInKey(accessTokenSecret), SignatureAlgorithm.HS256)
                .compact();

        return new TokenDetailDTO(token, timeInSec);
    }

    private TokenDetailDTO generateRefreshToken(String userId) {
        long timeInSec = refreshTokenTime * 60;

        String token = Jwts
                .builder()
                .setClaims(Map.of("jti", UUID.randomUUID()))
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeInSec * 1000))
                .signWith(getSignInKey(refreshTokenSecret), SignatureAlgorithm.HS256)
                .compact();

        return new TokenDetailDTO(token, timeInSec);
    }

    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenValid(String token, String secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(secretKey))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
