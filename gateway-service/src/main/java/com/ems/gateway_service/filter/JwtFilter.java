package com.ems.gateway_service.filter;

import com.ems.gateway_service.exceptions.CustomErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private final String secretKey;
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/employee-service/swagger-ui/**",
            "/api/employee-service/v3/api-docs/**"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtFilter(@Value("${jwt.accessTokenSecret}") String secretKey) {
        super(Config.class);
        this.secretKey = secretKey;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String requestPath = exchange.getRequest().getURI().getPath();
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (isExcludedPath(requestPath)) {
                log.debug("Path excluded from JWT validation: {}", requestPath);
                return chain.filter(exchange);
            }

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return sendErrorResponse(
                        exchange.getResponse(),
                        "Missing or invalid Authorization header",
                        HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSignInKey(secretKey))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                return chain.filter(exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-User-Role", claims.get("role", String.class))
                                .build())
                        .build());

            } catch (ExpiredJwtException e) {
                log.error("JWT token expired: {}", e.getMessage());
                return sendErrorResponse(exchange.getResponse(), "JWT token has expired", HttpStatus.UNAUTHORIZED);
            } catch (MalformedJwtException e) {
                log.error("Malformed JWT token: {}", e.getMessage());
                return sendErrorResponse(exchange.getResponse(), "Malformed JWT token", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                log.error("JWT token parsing failed: {}", e.getMessage());
                return sendErrorResponse(exchange.getResponse(), "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    public static class Config {}

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream()
                .anyMatch(pattern -> {
                    boolean matches = pathMatcher.match(pattern, path);
                    log.debug("Pattern: {} | Path: {} | Matches: {}", pattern, path, matches);
                    return matches;
                });
    }

    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Mono<Void> sendErrorResponse(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        CustomErrorResponse error = new CustomErrorResponse(message, "Unauthorized", status.value());
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(error);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            log.error("Error writing error response: {}", e.getMessage());
            return response.setComplete();
        }
    }
}