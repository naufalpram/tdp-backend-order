package com.edts.tdp.batch4.utils;

import com.edts.tdp.batch4.config.RsaKeyProperties;
import com.edts.tdp.batch4.exception.OrderCustomException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final RsaKeyProperties rsaKeys;

    public JwtUtil(RsaKeyProperties rsaKeys) {
        this.rsaKeys = rsaKeys;
    }

    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    public String extractUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts
                .parserBuilder()
//            .setSigningKey(secretKey)
                .setSigningKey(rsaKeys.publicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, String path) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (isTokenExpired(token)) {
                return false;
            }
            JWSVerifier verifier = new RSASSAVerifier(rsaKeys.publicKey());
            return signedJWT.verify(verifier);
        } catch (ParseException | JOSEException e) {
            throw new OrderCustomException(HttpStatus.BAD_REQUEST, e.getMessage(), path);
        }
    }
}
