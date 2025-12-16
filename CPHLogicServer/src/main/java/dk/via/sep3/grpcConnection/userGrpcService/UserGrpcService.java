package dk.via.sep3.grpcConnection.userGrpcService;

import dk.via.sep3.model.domain.User;

public interface UserGrpcService
{
  User getUserByUsername(String username);
  User createUser(User user);
}
