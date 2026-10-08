package it.unical.ea_project.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;
    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000L;          // 15 minuti
    private static final long REFRESH_TOKEN_VALIDITY = 7L * 24 * 60 * 60 * 1000; // 7 giorni
    @PostConstruct private void validateJwtKey() {getSigningKey();}

    private String buildToken(Map<String, Object> extraClaims, String subject, long expirationMillis) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    public String generateAccessToken(User user) {
        if (user.getRole() == null) {throw new IllegalStateException("Impossibile generare il token: ruolo utente mancante.");}
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TYPE_ACCESS);
        claims.put("role", user.getRole().name());
        claims.put("id", user.getId());
        return buildToken(claims, user.getEmail(), ACCESS_TOKEN_VALIDITY);
    }



    @Override
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TYPE_REFRESH);
        claims.put("id", user.getId());
        return buildToken(claims, user.getEmail(), REFRESH_TOKEN_VALIDITY);
    }

    @Override
    public Claims parseToken(String token, String expectedType) {
        Claims claims = extractAllClaims(token);
        if (!expectedType.equals(claims.get("type", String.class))) {
            throw new JwtException("Tipo di token non valido");
        }
        return claims;
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equalsIgnoreCase(user.getEmail()) || username.equalsIgnoreCase(user.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}