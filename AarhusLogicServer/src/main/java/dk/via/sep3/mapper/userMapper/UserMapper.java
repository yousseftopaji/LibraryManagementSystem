package dk.via.sep3.mapper.userMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.DTOs.login.LoginRequestDTO;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.DTOs.auth.RegisterResponseDTO;
import dk.via.sep3.DTOs.registration.RegistrationDTO;
import dk.via.sep3.DTOs.user.UserDTO;

public interface UserMapper {


    User mapRegistrationDTOToDomain(RegistrationDTO dto);

    UserDTO mapDomainToUserDTO(User user);

    User mapDTOUserToDomain(UserDTO dto);

    User mapDTOUserToDomain(DTOUser dto);

    RegisterResponseDTO mapDomainToRegisterResponseDTO(User user);

    DTOUser mapDomainToDTOUser(User user);

    User mapLoginRequestToDomain(LoginRequestDTO request);
}
