package dk.via.sep3.mapper.userMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.DTOs.login.LoginResponseDTO;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.DTOs.auth.RegisterResponseDTO;
import dk.via.sep3.DTOs.login.LoginRequestDTO;
import dk.via.sep3.DTOs.registration.RegistrationDTO;
import dk.via.sep3.DTOs.user.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User mapRegistrationDTOToDomain(RegistrationDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        return user;
    }

    @Override
    public UserDTO mapDomainToUserDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setFullName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());      // for internal use / gRPC only
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        return dto;
    }

    @Override
    public User mapDTOUserToDomain(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(dto.getRole());
        return user;
    }

    // New overload to map generated proto DTOUser -> domain User
    @Override
    public User mapDTOUserToDomain(DTOUser dto) {
        if (dto == null) return null;
        User user = new User();
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(dto.getRole());
        return user;
    }

    @Override
    public RegisterResponseDTO mapDomainToRegisterResponseDTO(User user) {
        if (user == null) return null;

        return new RegisterResponseDTO(
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }

    @Override
    public DTOUser mapDomainToDTOUser(User user) {
        if (user == null) return null;

        DTOUser.Builder builder = DTOUser.newBuilder();
        if (user.getUsername() != null) builder.setUsername(user.getUsername());
        if (user.getPassword() != null) builder.setPassword(user.getPassword());
        if (user.getRole() != null) builder.setRole(user.getRole());
        if (user.getName() != null) builder.setName(user.getName());
        if (user.getPhoneNumber() != null) builder.setPhoneNumber(user.getPhoneNumber());
        if (user.getEmail() != null) builder.setEmail(user.getEmail());
        return builder.build();
    }

    @Override
    public User mapLoginRequestToDomain(LoginRequestDTO request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return user;
    }
}
