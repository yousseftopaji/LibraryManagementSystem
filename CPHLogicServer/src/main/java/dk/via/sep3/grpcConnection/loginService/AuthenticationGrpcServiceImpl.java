package dk.via.sep3.grpcConnection.loginService;

import dk.via.sep3.LoginRequest;
import dk.via.sep3.LoginResponse;
import dk.via.sep3.AuthenticationServiceGrpc;
import dk.via.sep3.controller.exceptionHandler.AuthenticationFailedException;
import dk.via.sep3.controller.exceptionHandler.GrpcCommunicationException;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationGrpcServiceImpl implements AuthenticationGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(AuthenticationGrpcService.class);
  private final AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authStub;

  public AuthenticationGrpcServiceImpl(ManagedChannel channel)
  {
    this.authStub = AuthenticationServiceGrpc.newBlockingStub(channel);
  }

  @Override
  public String login(String username, String password) {
    // should receive a domain (user) object, map to proto DTO
    try {
      LoginRequest request = LoginRequest.newBuilder()
          .setUsername(username)
          .setPassword(password)
          .build();

      logger.info("Sending gRPC request to login user: {}", username);
      LoginResponse response = authStub.login(request);
      // got a response (proto dto), map back to domain object
      if (response.getSuccess()) {
        logger.info("User logged in successfully: {}", username);
        return response.getToken();
      } else {
        logger.error("Failed to login user: {}", response.getMessage());
        throw new AuthenticationFailedException("Invalid username or password");
      }
    } catch (AuthenticationFailedException ex) {
      throw ex;
    } catch (Exception ex) {
      logger.error("Error logging in user: {}", username, ex);
      throw new GrpcCommunicationException("Failed to communicate with authentication service", ex);
    }
  }
}

