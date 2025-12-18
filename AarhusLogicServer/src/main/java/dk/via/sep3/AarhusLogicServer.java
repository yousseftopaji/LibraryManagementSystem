package dk.via.sep3;

/**
 * Spring Boot application entry point for Aarhus Logic Server.
 *
 * @author Group 7
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class AarhusLogicServer
{
  public static void main(String[] args)
  {
    SpringApplication.run( AarhusLogicServer.class, args );
  }
}
