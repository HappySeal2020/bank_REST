package com.example.bankcards.service;

import com.example.bankcards.service.impl.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing JWT
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    private final String ACCESS_SECRET;
    private final String REFRESH_SECRET;

    public JwtServiceImpl(@Value ("${app.crypto.access-secret}") String acc,
                          @Value("${app.crypto.refresh-secret}") String ref){
        this.ACCESS_SECRET = acc;
        this.REFRESH_SECRET = ref;
    }

    public String generateToken(UserDetails user, long expiration, String secret, String type) {

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roles)
                .claim("type", type)
                .claim("jti", UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateAccessToken(UserDetails user) {
        return generateToken(user, 1000 * 60 * 15, ACCESS_SECRET, "access"); // 15 мин
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        return generateToken(user, 1000L * 60 * 60 * 24 * 7, REFRESH_SECRET,"refresh"); // 7 дней
    }


    @Override
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        try {
            // пробуем как access
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey(ACCESS_SECRET))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // если не получилось — пробуем refresh
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey(REFRESH_SECRET))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
    }

    @Override
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaims(token, REFRESH_SECRET);
            return !isExpired(claims)
                    && "refresh".equals(claims.get("type"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    private boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
    private Claims getClaims(String token, String secret) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public List<String> extractRoles(String token) {
        Claims claims = getClaims(token);
        return claims.get("roles", List.class);
    }

    @Override
    public String extractJti(String token) {
        return getClaims(token, REFRESH_SECRET).get("jti", String.class);
    }

    @Override
    public String extractTokenType(String token) {
        return Optional.ofNullable(extractAllClaims(token).get("type", String.class))
                .orElse("unknown");
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
