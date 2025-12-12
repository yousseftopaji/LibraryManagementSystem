package dk.via.sep3.grpcConnection.userGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.controller.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.mapper.userMapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcServiceImpl implements UserGrpcService {
    private static final Logger logger = LoggerFactory.getLogger(
            UserGrpcServiceImpl.class);
    private final UserServiceGrpc.UserServiceBlockingStub userStub;
    private final UserMapper userMapper;

    public UserGrpcServiceImpl(UserServiceGrpc.UserServiceBlockingStub userStub, UserMapper userMapper) {
        this.userStub = userStub;
        this.userMapper = userMapper;
    }

    @Override
    public User createUser(User user) {
        DTOUser dtoUser = userMapper.mapDomainToDTOUser(user);
        logger.info("Creating user via gRPC: {}", dtoUser);
        try {
            CreateUserRequest request = CreateUserRequest.newBuilder()
                    .setUser(dtoUser)
                    .build();
            CreateUserResponse response = userStub.createUser(request);
            if (response.getSuccess())
            {
                logger.info(response.getMessage());
            }
            return userMapper.mapDTOUserToDomain(response.getUser());
        } catch (Exception ex) {
            throw new GrpcCommunicationException(ex.getMessage());
        }
    }

    @Override
    public User getUserByUsername(String username) {
        logger.info("Validating if user exists with username: {}", username);
        try {
            GetUserByUsernameRequest request = GetUserByUsernameRequest
                    .newBuilder().setUsername(username).build();

            GetUserByUsernameResponse response = userStub
                    .getUserByUsername(request);

            if (response.getSuccess())
            {logger.info(response.getMessage());
                return userMapper.mapDTOUserToDomain(response.getUser());
            }

            // If the response indicates failure or the returned user is empty,
            // treat it as "not found" and return null so higher-level validators
            // won't assume the username exists.
            logger.info("User not found for username: {}. Response message: {}", username, response.getMessage());
            return null;
        } catch (Exception ex) {
            logger.error("Error fetching user by username: {}", username, ex);
            throw new GrpcCommunicationException(ex.getMessage());
        }
    }
}
