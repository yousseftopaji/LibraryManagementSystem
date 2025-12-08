package dk.via.sep3.grpcConnection.loginService;

import dk.via.sep3.LoginRequest;
import dk.via.sep3.LoginResponse;
import dk.via.sep3.AuthenticationServiceGrpc;
import dk.via.sep3.security.JwtTokenProvider;
import dk.via.sep3.security.PasswordEncoderService;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationGrpcServiceImpl implements AuthenticationGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(AuthenticationGrpcService.class);
  private final AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authStub;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoderService passwordEncoderService;

  public AuthenticationGrpcServiceImpl(ManagedChannel channel, JwtTokenProvider jwtTokenProvider, PasswordEncoderService passwordEncoderService)
  {
    this.authStub = AuthenticationServiceGrpc.newBlockingStub(channel);
    this.jwtTokenProvider = jwtTokenProvider;
    this.passwordEncoderService = passwordEncoderService;
  }

  @Override
  public String login(String username, String password) {
    try {
      LoginRequest request = LoginRequest.newBuilder()
          .setUsername(username)
          .setPassword(password)
          .build();

      logger.info("Sending gRPC request to login user: {}", username);
      LoginResponse response = authStub.login(request);

      if (response.getSuccess()) {
        logger.info("User logged in successfully: {}", username);
        return response.getToken();
      } else {
        logger.error("Failed to login user: {}", response.getMessage());
        return null;
      }
    } catch (Exception ex) {
      logger.error("Error logging in user: {}", username, ex);
      return null;
    }
  }
}

