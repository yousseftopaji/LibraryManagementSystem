package dk.via.sep3.shared.mapper.userMapper;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.auth.AuthResponseDTO;
import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.shared.user.UserDTO;
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
        // If you always register readers, you can hard-code here:
        // user.setRole("READER");
       // user.setRole(dto.getRole());
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

    @Override
    public AuthResponseDTO mapDomainToAuthResponse(User user, String jwtToken) {
        if (user == null) return null;

        return new AuthResponseDTO(
                jwtToken,
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }
}
