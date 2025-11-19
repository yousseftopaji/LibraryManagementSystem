package dk.via.sep3.model.users;

import dk.via.sep3.shared.user.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements  UserService
{
    @Override
    public UserDTO getUserByUsername(String username) {
        //Communicate with the GRPC connection, and retrieve the user from there
        return null;
    }
}
