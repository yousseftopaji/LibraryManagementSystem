package dk.via.sep3.grpcConnection.loginGrpcService;

import dk.via.sep3.model.domain.User;

public interface LoginGrpcService
{
  User login(String username, String password);
}
