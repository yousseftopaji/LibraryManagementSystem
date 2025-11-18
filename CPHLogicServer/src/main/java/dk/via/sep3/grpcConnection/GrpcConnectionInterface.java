package dk.via.sep3.grpcConnection;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOLoan;

import java.util.List;

public interface GrpcConnectionInterface
{
  public List<DTOBook> getAllBooks();
  public List<DTOBook> getBooksByIsbn(String isbn);
  public DTOLoan createLoan(String username, String bookId, int loanDurationDays);
}
