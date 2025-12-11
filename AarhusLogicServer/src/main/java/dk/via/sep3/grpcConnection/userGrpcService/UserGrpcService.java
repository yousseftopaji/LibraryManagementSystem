package dk.via.sep3.grpcConnection.userGrpcService;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.registration.RegistrationDTO;

public interface UserGrpcService
{
    User createUser(User user);

    User getUserByUsername(String username);
}
