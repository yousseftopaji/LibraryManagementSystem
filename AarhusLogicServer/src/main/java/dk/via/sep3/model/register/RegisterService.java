package dk.via.sep3.model.register;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.registration.RegistrationDTO;

public interface RegisterService {
    User register(User user);
}
