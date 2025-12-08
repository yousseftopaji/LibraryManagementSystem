package dk.via.sep3.grpcConnection.loginService;

public interface AuthenticationGrpcService
{
  String login(String username, String password);
}
