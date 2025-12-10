package dk.via.sep3.grpcConnection.registrationService;

import dk.via.sep3.shared.registration.CreateRegisterDTO;

public interface RegistrationGrpcService
{
  void register(CreateRegisterDTO createRegisterDTO);
}
