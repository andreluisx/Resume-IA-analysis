package dev.andre.ResumeAiAnalysis.Config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(UserEntity user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("userId", user.getId())
                .withClaim("email", user.getEmail())
                .withClaim("name", user.getName())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(86400)))
                .withIssuedAt(Date.from(Instant.now()))
                .withIssuer("API movieflix")
                .sign(algorithm);

    }

    public Optional<JWTUserData> validate(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT jwt = JWT.require(algorithm)
                    .build()
                    .verify(token);


            JWTUserData build = JWTUserData.builder()
                    .id(jwt.getClaim("userId").asLong())
                    .email(jwt.getSubject())
                    .name(jwt.getClaim("name").asString())
                    .build();

            return Optional.of(build);
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
}
