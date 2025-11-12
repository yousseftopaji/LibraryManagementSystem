package dk.via.sep3.grpcConnection;

import dk.via.sep3.CreateLoanResponse;
import dk.via.sep3.DTOBook;
import dk.via.sep3.model.entities.CreateLoanDTO;

import java.util.List;

public interface GrpcConnectionInterface
{
  public List<DTOBook> getAllBooks();
  public DTOBook getBook(String isbn);
  public CreateLoanResponse createLoan(CreateLoanDTO createLoanDTO);
}
