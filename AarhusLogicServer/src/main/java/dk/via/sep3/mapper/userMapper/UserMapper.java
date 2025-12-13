package dk.via.sep3.mapper.userMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.DTOs.login.LoginRequestDTO;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.DTOs.auth.AuthResponseDTO;
import dk.via.sep3.DTOs.registration.RegistrationDTO;
import dk.via.sep3.DTOs.user.UserDTO;

public interface UserMapper {


    User mapRegistrationDTOToDomain(RegistrationDTO dto);

    // gRPC / shared UserDTO <-> domain User
    UserDTO mapDomainToUserDTO(User user);

    User mapDTOUserToDomain(UserDTO dto);

    // Overloaded method to map generated proto DTOUser -> domain User
    User mapDTOUserToDomain(DTOUser dto);

    // Domain User + JWT -> Auth response for client
    AuthResponseDTO mapDomainToAuthResponse(User user);

    DTOUser mapDomainToDTOUser(User user);

    User mapLoginRequestToDomain(LoginRequestDTO request);
}
