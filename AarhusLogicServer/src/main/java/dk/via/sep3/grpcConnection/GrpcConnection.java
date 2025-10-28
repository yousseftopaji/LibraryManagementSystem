package dk.via.sep3.grpcConnection;

import dk.via.sep3.BookServiceGrpc;
import dk.via.sep3.DTOBook;
import dk.via.sep3.GetAllBooksRequest;
import dk.via.sep3.GetAllBooksResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GrpcConnection implements GrpcConnectionInterface
{
  private final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",
      9090).usePlaintext().build();

  private final BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(
      channel);

  public List<DTOBook> getAllBooks()
  {
    try
    {
      GetAllBooksRequest request = GetAllBooksRequest.newBuilder().build();
      System.out.println("Sending gRPC request to get all books...");
      GetAllBooksResponse response = stub.getAllBooks(request);
      return response.getBooksList();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return new ArrayList<>();
    }
  }
}
