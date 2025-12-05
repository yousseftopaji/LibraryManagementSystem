package dk.via.sep3.grpcConnection;

import dk.via.sep3.shared.user.UserDTO;
import dk.via.sep3.shared.registration.RegistrationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class GrpcPersistenceClient implements PersistenceClient {
    private final dk.via.sep3.UserServiceGrpc.UserServiceBlockingStub stub;

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcPersistenceClient.class.getName());


    public GrpcPersistenceClient(dk.via.sep3.UserServiceGrpc.UserServiceBlockingStub stub) {
        this.stub = stub;
    }

    @Override
    public boolean usernameExists(String username) {
        dk.via.sep3.UsernameCheckRequest req = dk.via.sep3.UsernameCheckRequest.newBuilder().setUsername(username).build();
        dk.via.sep3.UsernameCheckResponse resp = stub.checkUsername(req);
        return resp.getExists();
    }

    @Override
    public boolean createUser(RegistrationDTO registrationDTO) {
        // Assume registrationDTO.password is already hashed by the logic layer
        dk.via.sep3.RegistrationRequest req = dk.via.sep3.RegistrationRequest.newBuilder()
                .setFullName(registrationDTO.getFullName() == null ? "" : registrationDTO.getFullName())
                .setEmail(registrationDTO.getEmail() == null ? "" : registrationDTO.getEmail())
                .setPhone(registrationDTO.getPhone() == null ? "" : registrationDTO.getPhone())
                .setUsername(registrationDTO.getUsername() == null ? "" : registrationDTO.getUsername())
                .setPassword(registrationDTO.getPassword() == null ? "" : registrationDTO.getPassword())
                .build();
        dk.via.sep3.RegistrationResponse resp = stub.register(req);
        return resp.getSuccess();
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        dk.via.sep3.GetUserByUsernameRequest req = dk.via.sep3.GetUserByUsernameRequest.newBuilder().setUsername(username).build();
        dk.via.sep3.GetUserByUsernameResponse resp = stub.getUserByUsername(req);
        if (!resp.getSuccess()) return null;
        dk.via.sep3.DTOUser dtoUser = resp.getUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(dtoUser.getUsername());
        // library.proto's DTOUser currently only contains username. Full name/email are not provided by proto.
        return userDTO;
    }
}
