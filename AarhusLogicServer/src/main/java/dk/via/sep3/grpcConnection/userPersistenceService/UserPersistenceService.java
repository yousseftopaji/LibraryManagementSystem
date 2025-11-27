package dk.via.sep3.grpcConnection.userPersistenceService;

import dk.via.sep3.DTOUser;

public interface UserPersistenceService
{
  DTOUser getUserByUsername(String username);
}
