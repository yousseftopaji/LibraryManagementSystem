package dk.via.sep3.model.utils.validation;

import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import org.springframework.stereotype.Service;

@Service
public class ValidatorImpl implements Validator
{
    private final UserGrpcService userGrpcService;

    public ValidatorImpl(UserGrpcService userGrpcService)
    {
        this.userGrpcService = userGrpcService;
    }

    @Override public void validateUser(String username)
    {
        if (userGrpcService.getUserByUsername(username) == null)
        {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }
}