package dk.via.sep3.grpcConnection;

import dk.via.sep3.BookServiceGrpc;
import dk.via.sep3.DTOBook;
import dk.via.sep3.GetAllBooksRequest;
import dk.via.sep3.GetAllBooksResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;

public class GrpcConnection implements GrpcConnectionInterface
{
  //I want to check my connection to this server
  private ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",
      9090).usePlaintext().build();


  private BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(
      channel);

  public List<DTOBook> getAllBooks()
  {
    try
    {
      GetAllBooksRequest request = GetAllBooksRequest.newBuilder().build();
      System.out.println("Sending gRPC request to get all books...");
      GetAllBooksResponse response = stub.getAllBooks(request);
      System.out.println(response.getBooksList() + " <- Received gRPC response with all books.");
      return response.getBooksList();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return new ArrayList<>();
    }
  }
}
