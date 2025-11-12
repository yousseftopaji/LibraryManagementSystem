package dk.via.sep3.model;

import dk.via.sep3.CreateLoanResponse;
import dk.via.sep3.DTOBook;
import dk.via.sep3.grpcConnection.GrpcConnectionInterface;
import dk.via.sep3.model.entities.CreateLoanDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Model implements BookList
{
  private final GrpcConnectionInterface grpcConnectionInterface;

  public Model(GrpcConnectionInterface grpcConnectionInterface)
  {
    this.grpcConnectionInterface = grpcConnectionInterface;
  }

  @Override
  public List<DTOBook> getAllBooks()
  {
    return grpcConnectionInterface.getAllBooks();
  }

  @Override
  public DTOBook getBook(String isbn)
  {
    return grpcConnectionInterface.getBook(isbn);
  }

  @Override
  public CreateLoanResponse createLoan(CreateLoanDTO createLoanDTO)
  {
    return grpcConnectionInterface.createLoan(createLoanDTO);
  }
}
