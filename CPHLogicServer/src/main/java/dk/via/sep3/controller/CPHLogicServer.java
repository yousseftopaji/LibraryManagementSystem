package dk.via.sep3.controller;

import dk.via.sep3.grpcConnection.*;

import dk.via.sep3.model.books.BookService;
import dk.via.sep3.model.books.BookServiceImpl;
import dk.via.sep3.model.loans.LoanService;
import dk.via.sep3.model.loans.LoanServiceImpl;
import dk.via.sep3.model.users.UserService;
import dk.via.sep3.model.users.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CPHLogicServer
{
  public static void main(String[] args)
  {
    SpringApplication.run( CPHLogicServer.class, args );
  }
  @Bean
  public BookService bookService()
  {
      return new BookServiceImpl(grpcConnectionInterface());

  }

  @Bean
    public GrpcConnectionInterface grpcConnectionInterface()
    {
        return new GrpcConnection();
    }

    @Bean
    public LoanService loanService()
    {
        return new LoanServiceImpl( grpcConnectionInterface(), userService() );
    }

    @Bean
    public UserService userService()
    {
        return new UserServiceImpl();
    }
}
