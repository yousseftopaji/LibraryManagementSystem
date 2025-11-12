package dk.via.sep3.controller;

import dk.via.sep3.CreateLoanResponse;
import dk.via.sep3.model.BookList;
import dk.via.sep3.model.entities.CreateLoanDTO;
import dk.via.sep3.model.entities.LoanResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LoansController
{
  private final BookList bookList;

  public LoansController(BookList bookList)
  {
    this.bookList = bookList;
  }

  @PostMapping
  public ResponseEntity<LoanResponseDTO> createLoan(@RequestBody CreateLoanDTO createLoanDTO)
  {
    try
    {
      System.out.println("Received create loan request for ISBN: " + createLoanDTO.getBookISBN() +
                         ", Username: " + createLoanDTO.getUsername());

      CreateLoanResponse grpcResponse = bookList.createLoan(createLoanDTO);

      if (grpcResponse == null)
      {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      LoanResponseDTO loanResponseDTO = new LoanResponseDTO(
          grpcResponse.getLoanId(),
          grpcResponse.getBookId(),
          grpcResponse.getIsbn(),
          grpcResponse.getUserId(),
          grpcResponse.getLoanDate(),
          grpcResponse.getDueDate()
      );

      return new ResponseEntity<>(loanResponseDTO, HttpStatus.CREATED);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

