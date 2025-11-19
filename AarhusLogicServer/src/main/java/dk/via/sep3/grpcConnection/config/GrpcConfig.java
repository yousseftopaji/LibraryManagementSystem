package dk.via.sep3.grpcConnection.config;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {
  @Value("${grpc.server.host:localhost}")
  private String host;

  @Value("${grpc.server.port:5020}")
  private int port;

  @Bean
  public ManagedChannel managedChannel() {
    return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
  }
}
