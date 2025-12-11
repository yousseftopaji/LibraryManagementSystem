package dk.via.sep3.grpcConnection.registrationGrpcService;

import dk.via.sep3.model.domain.User;

public interface RegistrationGrpcService
{
  User register(User user);
}
