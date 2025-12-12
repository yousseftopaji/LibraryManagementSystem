package dk.via.sep3.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final BCryptPasswordEncoder encoder;

    public PasswordService(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public String hash(String raw) {
        return encoder.encode(raw);
    }

    public boolean matches(String raw, String hash) {
        return encoder.matches(raw, hash);
    }
}

