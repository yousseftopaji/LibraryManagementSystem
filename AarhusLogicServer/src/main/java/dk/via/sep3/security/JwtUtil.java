package dk.via.sep3.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class JwtUtil implements IJwtTokenProvider {

    @Value("${jwt.secret:sep3defaultsecret}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secret.getBytes());
        verifier = JWT.require(algorithm).build();
    }

    @Override
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);

    }

    // keep old helper for backwards compatibility
    public String extractUsername(String token) {
        DecodedJWT decoded = verifier.verify(token);
        return decoded.getSubject();
    }

    // keep old helper for backwards compatibility
    public String extractRole(String token) {
        DecodedJWT decoded = verifier.verify(token);
        return decoded.getClaim("role").asString();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }

    @Override
    public String getRoleFromToken(String token) {
        return extractRole(token);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
