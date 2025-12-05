package dk.via.sep3.model.users;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcServiceImpl;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.user.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {
    private final UserGrpcService userGrpcService;


    public UserServiceImpl(UserGrpcService userGrpcService) {
        this.userGrpcService = userGrpcService;
    }

    @Override
    public User getUserByUsername(String username) {
        if (!StringUtils.hasText(username))
        {
            throw new BusinessRuleViolationException("Username must not be null or empty");
        }
        return userGrpcService.getUserByUsername(username);
    }

}
