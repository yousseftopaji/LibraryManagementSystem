package dk.via.sep3.grpcConnection.userGrpcService;

import dk.via.sep3.DTOUser;

public interface UserGrpcService
{
  DTOUser getUserByUsername(String username);
}
