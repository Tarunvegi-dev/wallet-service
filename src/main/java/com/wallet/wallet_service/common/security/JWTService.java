package com.wallet.wallet_service.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
    @Value("${jwt.token.secret}")
    private String secretKey;

    @Value("${jwt.token.expiration}")
    private Long expirationTime;

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId){
        return Jwts.builder()
                   .signWith(getKey(), SignatureAlgorithm.HS256)
                   .setSubject(userId.toString())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                   .compact();
    }

    private Claims extractAlClaims(String token){
        return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }

    public String extractUserId(String token){
        return extractAlClaims(token).getSubject();
    }

    public Date extractExpirationTime(String token){
        return extractAlClaims(token).getExpiration();
    }

    public Boolean validateToken(String token){
        Date expDate = extractExpirationTime(token);
        return expDate.after(new Date());
    }
}
