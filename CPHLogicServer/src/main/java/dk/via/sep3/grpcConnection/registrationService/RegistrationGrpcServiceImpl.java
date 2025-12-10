package dk.via.sep3.grpcConnection.registrationService;

import dk.via.sep3.RegisterRequest;
import dk.via.sep3.RegisterResponse;
import dk.via.sep3.RegistrationServiceGrpc;
import dk.via.sep3.controller.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.controller.exceptionHandler.UserAlreadyExistsException;
import dk.via.sep3.security.PasswordEncoderService;
import dk.via.sep3.shared.registration.CreateRegisterDTO;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistrationGrpcServiceImpl implements RegistrationGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(RegistrationGrpcService.class);
  private final RegistrationServiceGrpc.RegistrationServiceBlockingStub registrationStub;
  private final PasswordEncoderService passwordEncoderService;

  public RegistrationGrpcServiceImpl(ManagedChannel channel, PasswordEncoderService passwordEncoderService)
  {
    this.registrationStub = RegistrationServiceGrpc.newBlockingStub(channel);
    this.passwordEncoderService = passwordEncoderService;
  }

  @Override
  public void register(CreateRegisterDTO createRegisterDTO) {
    try {
      String hashedPassword = passwordEncoderService.encode(createRegisterDTO.getPassword());

      RegisterRequest request = RegisterRequest.newBuilder()
          .setFullName(createRegisterDTO.getFullName())
          .setEmail(createRegisterDTO.getEmail())
          .setPhoneNumber(createRegisterDTO.getPhoneNumber())
          .setUsername(createRegisterDTO.getUsername())
          .setPassword(hashedPassword)
          .build();

      logger.info("Sending gRPC request to register user: {}", createRegisterDTO.getUsername());
      RegisterResponse response = registrationStub.register(request);

      if (response.getSuccess()) {
        logger.info("User registered successfully: {}", createRegisterDTO.getUsername());
      } else {
        logger.error("Failed to register user: {}", response.getMessage());
        throw new UserAlreadyExistsException("Username already in use");
      }
    } catch (UserAlreadyExistsException ex) {
      throw ex;
    } catch (Exception ex) {
      logger.error("Error registering user: {}", createRegisterDTO.getUsername(), ex);
      throw new GrpcCommunicationException("Failed to communicate with registration service", ex);
    }
  }
}
