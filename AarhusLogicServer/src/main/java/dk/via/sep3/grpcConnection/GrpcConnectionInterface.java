package dk.via.sep3.grpcConnection;

import dk.via.sep3.DTOBook;

import java.util.List;

public interface GrpcConnectionInterface
{
  public List<DTOBook> getAllBooks();
}
