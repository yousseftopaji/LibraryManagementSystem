package dk.via.sep3.model.loans;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOLoan;
import dk.via.sep3.grpcConnection.GrpcConnectionInterface;
import dk.via.sep3.model.users.UserService;
import dk.via.sep3.shared.LoanDTO;
import dk.via.sep3.shared.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanServiceImpl implements LoanService
{
  private final GrpcConnectionInterface grpcConnectionInterface;
  private final UserService userService;

  public LoanServiceImpl(GrpcConnectionInterface grpcConnectionInterface, UserService userService)
  {
    this.grpcConnectionInterface = grpcConnectionInterface;
    this.userService = userService;
  }

  @Override
  public LoanDTO createLoan(String username, String bookId, int loanDurationDays)
  {
    // Validate DATES - loan duration must be positive
    if (loanDurationDays <= 0)
    {
      throw new IllegalArgumentException("Loan duration must be positive. Provided: " + loanDurationDays + " days");
    }

    // Validate USER exists
    UserDTO user = userService.getUserByUsername(username);
    if (user == null)
    {
      throw new IllegalArgumentException("User not found with username: " + username);
    }

    // Validate BOOK exists and is available
    List<DTOBook> allBooks = grpcConnectionInterface.getAllBooks();
    DTOBook targetBook = null;

    for (DTOBook book : allBooks)
    {
      if (String.valueOf(book.getId()).equals(bookId))
      {
        targetBook = book;
        break;
      }
    }

    if (targetBook == null)
    {
      throw new IllegalArgumentException("Book not found with ID: " + bookId);
    }

    if (!targetBook.getState().equals("AVAILABLE"))
    {
      throw new IllegalStateException("Book with ID: " + bookId + " is not available. Current state: " + targetBook.getState());
    }

    // All validations passed, create the loan via gRPC
    DTOLoan grpcLoan = grpcConnectionInterface.createLoan(username, bookId, loanDurationDays);

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

