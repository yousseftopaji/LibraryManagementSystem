package dk.via.sep3.grpcConnection;

import dk.via.sep3.DTOBook;
import dk.via.sep3.model.entities.BookDTO;
import dk.via.sep3.model.entities.LoanDTO;
import dk.via.sep3.model.entities.ReservationDTO;

import java.util.List;

public interface GrpcConnectionInterface {
    BookDTO getBookByIsbn(String isbn);
    LoanDTO createLoan(String username, String isbn);
    ReservationDTO reserveBook(String username, String isbn);

    List<DTOBook> getAllBooks();
}
