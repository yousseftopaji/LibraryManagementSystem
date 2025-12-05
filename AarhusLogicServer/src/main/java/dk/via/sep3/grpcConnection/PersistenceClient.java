package dk.via.sep3.grpcConnection;

import dk.via.sep3.shared.user.UserDTO;
import dk.via.sep3.shared.registration.RegistrationDTO;

public interface PersistenceClient {
    boolean usernameExists(String username);
    boolean createUser(RegistrationDTO registrationDTO);
    UserDTO getUserByUsername(String username);
}
