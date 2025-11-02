package com.journal.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;

    public JwtService(
            @Value("${security.jwt.secret:QmFja3VwS2V5Rm9yRGV2ZWxvcG1lbnRSZXBsYWNlVGhpcw==}") String secret,
            @Value("${security.jwt.access-ttl-minutes:60}") long accessTokenMinutes,
            @Value("${security.jwt.refresh-ttl-hours:24}") long refreshTokenHours) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenTtl = Duration.ofMinutes(accessTokenMinutes);
        this.refreshTokenTtl = Duration.ofHours(refreshTokenHours);
    }

    public String generateAccessToken(UserDetails user, Map<String, Object> extraClaims) {
        return buildToken(extraClaims, user, accessTokenTtl);
    }

    public String generateRefreshToken(UserDetails user) {
        return buildToken(Map.of("type", "refresh"), user, refreshTokenTtl);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    Claims claims = Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
        return resolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private String buildToken(Map<String, Object> claims, UserDetails user, Duration ttl) {
        Instant now = Instant.now();
    return Jwts.builder()
        .claims(claims)
        .subject(user.getUsername())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(ttl)))
        .signWith(signingKey)
        .compact();
    }
}
