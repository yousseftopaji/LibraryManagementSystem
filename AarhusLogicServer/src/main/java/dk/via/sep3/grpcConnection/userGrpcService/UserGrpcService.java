package dk.via.sep3.grpcConnection.userGrpcService;

import dk.via.sep3.application.domain.User;

public interface UserGrpcService
{
    User createUser(User user);

    User getUserByUsername(String username);
}
