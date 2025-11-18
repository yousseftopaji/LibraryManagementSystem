package dk.via.sep3.model.loans;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOLoan;
import dk.via.sep3.DTOUser;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.shared.CreateLoanDTO;
import dk.via.sep3.shared.LoanDTO;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService
{
  private final BookGrpcService bookGrpcService;
  private final LoanGrpcService loanGrpcService;
  private final UserGrpcService userGrpcService;

  public LoanServiceImpl(BookGrpcService bookGrpcService, LoanGrpcService loanGrpcService, UserGrpcService userGrpcService)
  {
    this.bookGrpcService = bookGrpcService;
    this.loanGrpcService = loanGrpcService;
    this.userGrpcService = userGrpcService;
  }

  @Override
  public LoanDTO createLoan(CreateLoanDTO createLoanDTO)
  {
    // Validate USER exists
    DTOUser user = userGrpcService.getUserByUsername(createLoanDTO.getUsername());
    if (user == null)
    {
      throw new IllegalArgumentException("User not found with username: " + createLoanDTO.getUsername());
    }

    // Validate BOOK exists and is available
    List<DTOBook> books = bookGrpcService.getBooksByIsbn(createLoanDTO.getBookISBN());
    DTOBook targetBook = null;
    for (DTOBook book : books)
    {
      if (book.getState().equalsIgnoreCase("AVAILABLE"))
      {
        targetBook = book;
        break;
      }
      throw new IllegalArgumentException("Book is not available");
    }
    if (targetBook == null)
    {
      throw new IllegalArgumentException("Book not found with ID: " + createLoanDTO.getBookISBN());
    }

    bookGrpcService.updateBookStatus(String.valueOf(targetBook.getId()), "Borrowedd");

    // Create a variable of today's date, that I can save in the db as a borrow date yyyy-mm-dd
    Date today = new Date(System.currentTimeMillis());
    Date borrowDate = Date.valueOf(today.toLocalDate());
    Date dueDate = Date.valueOf(today.toLocalDate().plusDays(30));

    // All validations passed, create the loan via gRPC
    DTOLoan grpcLoan = loanGrpcService.createLoan(
        createLoanDTO.getUsername(),
        String.valueOf(targetBook.getId()),
        borrowDate.toString(),
        dueDate.toString()
    );

    if (grpcLoan == null || grpcLoan.getId() <= 0)
    {
      throw new RuntimeException("Failed to create loan - received invalid response from server");
    }

    // Convert DTOLoan to LoanDTO and return
    return new LoanDTO(
        String.valueOf(grpcLoan.getId()),
        grpcLoan.getBorrowDate(),
        grpcLoan.getDueDate(),
        false, // isReturned - new loans are not returned
        0,     // numberOfExtensions - new loans have 0 extensions
        grpcLoan.getUsername(),
        String.valueOf(grpcLoan.getBookId())
    );
  }
}

