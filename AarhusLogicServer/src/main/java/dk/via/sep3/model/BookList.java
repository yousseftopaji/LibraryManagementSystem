package dk.via.sep3.model;

import dk.via.sep3.CreateLoanResponse;
import dk.via.sep3.DTOBook;
import dk.via.sep3.model.entities.CreateLoanDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookList
{
  List<DTOBook> getAllBooks();
  DTOBook getBook(String isbn);
  CreateLoanResponse createLoan(CreateLoanDTO createLoanDTO);
}
