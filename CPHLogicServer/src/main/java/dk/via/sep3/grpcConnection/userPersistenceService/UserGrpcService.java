package dk.via.sep3.grpcConnection.userPersistenceService;

import dk.via.sep3.DTOUser;

public interface UserGrpcService
{
  DTOUser getUserByUsername(String username);
}
