package dk.via.sep3.grpcConnection.config;


import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.File;

@Configuration
public class GrpcConfig {
  @Value("${grpc.server.host:localhost}")
  private String host;

  @Value("${grpc.server.port:5020}")
  private int port;

  // TLS configuration
  @Value("${grpc.tls.enabled:false}")
  private boolean tlsEnabled;

  @Value("${grpc.tls.trustCert:}")
  private String trustCertPath;

  @Value("${grpc.tls.clientCert:}")
  private String clientCertPath;

  @Value("${grpc.tls.clientKey:}")
  private String clientKeyPath;

  private ManagedChannel channel;

  @Bean
  public ManagedChannel managedChannel() {
    if (tlsEnabled) {
      try {
        NettyChannelBuilder builder = NettyChannelBuilder.forAddress(host, port);
        if (trustCertPath != null && !trustCertPath.isBlank()) {
          // Build an SslContext with the provided trust certificate (and optional client cert/key for mTLS)
          io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder sslBuilder = GrpcSslContexts.forClient();
          sslBuilder.sslProvider(SslProvider.JDK);
          sslBuilder.trustManager(new File(trustCertPath));
          if (clientCertPath != null && !clientCertPath.isBlank() && clientKeyPath != null && !clientKeyPath.isBlank()) {
            sslBuilder.keyManager(new File(clientCertPath), new File(clientKeyPath));
          }
          SslContext sslContext = sslBuilder.build();
          builder.sslContext(sslContext);
        } else {
          // Use default JVM trust store / system TLS settings
          builder.useTransportSecurity();
        }
        this.channel = builder.build();
        return this.channel;
      } catch (Exception e) {
        throw new RuntimeException("Failed to create TLS gRPC channel", e);
      }
    }

    // Default: plaintext (dev only)
    this.channel = io.grpc.ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    return this.channel;
  }

  @Bean
  public dk.via.sep3.UserServiceGrpc.UserServiceBlockingStub userServiceStub(ManagedChannel channel) {
    return dk.via.sep3.UserServiceGrpc.newBlockingStub(channel);
  }

  @PreDestroy
  public void shutdownChannel() {
    if (channel != null && !channel.isShutdown()) {
      channel.shutdownNow();
    }
  }
}
