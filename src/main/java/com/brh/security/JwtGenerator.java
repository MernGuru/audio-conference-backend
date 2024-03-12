package com.brh.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

public class JwtGenerator {
    public String createToken(String username, List<String> roles) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("YourSecretKeyForJWTSigning123123123123123123");

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(new Date(nowMillis + 3600000)) // 1 hour expiration
                .signWith(SignatureAlgorithm.HS256, apiKeySecretBytes)
                .compact();
    }

    public Claims parseToken(String jwt) {
        String secretString = "YourSecretKeyForJWTSigning123123123123123123";
        byte[] secretBytes = secretString.getBytes(StandardCharsets.UTF_8);
        Key signingKey = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

    }
}
