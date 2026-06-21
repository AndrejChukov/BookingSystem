package ru.bookingsystem.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {
    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    @Getter
    @Value("${spring.security.jwt.expiration-time}")
    private long expirationTime;

    @Value("${spring.security.jwt.algorithm}")
    private String algorithm;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(getSecretKey());
        return new NimbusJwtEncoder(jwkSource);
    }

    public SecretKey getSecretKey() {
        OctetSequenceKey key = new OctetSequenceKey.Builder(secretKey.getBytes())
                .algorithm(getJWSAlgorithm())
                .build();
        return key.toSecretKey();
    }

    public JWSAlgorithm getJWSAlgorithm() {
        return new JWSAlgorithm((algorithm));
    }
}
