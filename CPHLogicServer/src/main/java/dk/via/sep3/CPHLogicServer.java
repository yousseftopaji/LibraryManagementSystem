package dk.via.sep3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "dk.via.sep3")
public class CPHLogicServer
{
  public static void main(String[] args)
  {
    SpringApplication.run( CPHLogicServer.class, args );
  }
}
