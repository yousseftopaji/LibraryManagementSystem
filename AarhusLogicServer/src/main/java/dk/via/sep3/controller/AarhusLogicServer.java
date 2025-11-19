package dk.via.sep3.controller;


import dk.via.sep3.model.books.BookService;
import dk.via.sep3.model.books.BookServiceImpl;
import dk.via.sep3.model.loans.LoanService;
import dk.via.sep3.model.loans.LoanServiceImpl;
import dk.via.sep3.model.users.UserService;
import dk.via.sep3.model.users.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "dk.via.sep3")
public class AarhusLogicServer
{

  public static void main(String[] args)
  {
    SpringApplication.run( AarhusLogicServer.class, args );
  }

}
