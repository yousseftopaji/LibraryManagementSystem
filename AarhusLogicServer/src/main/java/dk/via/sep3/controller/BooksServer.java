package dk.via.sep3.controller;

import dk.via.sep3.grpcConnection.*;
import dk.via.sep3.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BooksServer
{
  public static void main(String[] args)
  {
    SpringApplication.run( BooksServer.class, args );
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
}
