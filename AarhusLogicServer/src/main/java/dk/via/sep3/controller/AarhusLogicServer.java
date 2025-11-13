package dk.via.sep3.controller;

import dk.via.sep3.grpcConnection.*;
import dk.via.sep3.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AarhusLogicServer
{
  public static void main(String[] args)
  {
    SpringApplication.run( AarhusLogicServer.class, args );
  }

  @Bean
  public GrpcConnectionInterface grpcConnectionInterface()
  {
    return new GrpcConnection();
  }

  @Bean
  public BookList bookList(GrpcConnectionInterface grpcConnectionInterface)
  {
    return new Model(grpcConnectionInterface);
  }

  @Bean
  public BooksController booksController(BookList bookList)
  {
    return new BooksController(bookList);
  }

  @Bean
  public LoanService loanService(GrpcConnectionInterface grpcConnectionInterface)
  {
    return new LoanServiceImpl(grpcConnectionInterface);
  }

  @Bean
  public LoansController loansController(LoanService loanService)
  {
    return new LoansController(loanService);
  }
}
