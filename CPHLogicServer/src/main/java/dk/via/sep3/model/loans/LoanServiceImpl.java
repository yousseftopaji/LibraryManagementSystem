package dk.via.sep3.model.loans;

import dk.via.sep3.*;
import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOUser;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.model.utils.validation.Validator;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service public class LoanServiceImpl implements LoanService
{
  private final BookGrpcService bookGrpcService;
  private final LoanGrpcService loanGrpcService;
  private final UserGrpcService userGrpcService;
  private final Validator validator;

  public LoanServiceImpl(BookGrpcService bookGrpcService,
      LoanGrpcService loanGrpcService, UserGrpcService userGrpcService,
      Validator validator)
  {
    this.bookGrpcService = bookGrpcService;
    this.loanGrpcService = loanGrpcService;
    this.userGrpcService = userGrpcService;

    this.validator = validator;
  }

  @Override public LoanDTO createLoan(CreateLoanDTO createLoanDTO)
  {
    // Validate USER exists
    DTOUser user = userGrpcService.getUserByUsername(
        createLoanDTO.getUsername());
    if (user == null)
    {
      throw new IllegalArgumentException(
          "User not found with username: " + createLoanDTO.getUsername());
    }

    // Validate BOOK exists and is available
    List<DTOBook> books = bookGrpcService.getBooksByIsbn(
        createLoanDTO.getBookISBN());
    DTOBook targetBook = null;
    System.out.println(books.size());
    for (DTOBook book : books)
    {
      if (book.getState().equalsIgnoreCase("AVAILABLE"))
      {
        targetBook = book;
        System.out.println("Found available book: " + book.getId());
        break;
      }
    }

    if (targetBook == null)
    {
      throw new IllegalArgumentException(
          "Book not found with ID: " + createLoanDTO.getBookISBN());
    }

    // Create a variable of today's date, that I can save in the db as a borrow date yyyy-mm-dd
    Date today = new Date(System.currentTimeMillis());
    Date borrowDate = Date.valueOf(today.toLocalDate());
    Date dueDate = Date.valueOf(today.toLocalDate().plusDays(30));

    // All validations passed, create the loan via gRPC
    DTOLoan grpcLoan = loanGrpcService.createLoan(createLoanDTO.getUsername(),
        String.valueOf(targetBook.getId()), borrowDate.toString(),
        dueDate.toString());

    if (grpcLoan == null || grpcLoan.getId() <= 0)
    {
      throw new RuntimeException(
          "Failed to create loan - received invalid response from server");
    }

    bookGrpcService.updateBookStatus(String.valueOf(targetBook.getId()),
        "Borrowed");
    // Convert DTOLoan to LoanDTO and return
    return new LoanDTO(String.valueOf(grpcLoan.getId()),
        grpcLoan.getBorrowDate(), grpcLoan.getDueDate(), false,
        // isReturned - new loans are not returned
        0,     // numberOfExtensions - new loans have 0 extensions
        grpcLoan.getUsername(), String.valueOf(grpcLoan.getBookId()));
  }

  @Override public void extendLoan(int loanId, String username)
  {
    // Validate USER exists
    DTOUser user = userGrpcService.getUserByUsername(username);
    if (user == null)
    {
      throw new IllegalArgumentException("User not found: " + username);
    }

    // Get LOAN by loanId
    DTOLoan loan = loanGrpcService.getLoanById(loanId);
    if (loan == null)
    {
      throw new IllegalArgumentException("Loan not found with ID: " + loanId);
    }

    // Max 12 extensions
    if (loan.getNumberOfExtensions() >= 12)
    {
      throw new IllegalStateException(
          "Loan has reached the maximum number of extensions.");
    }

    // Business rule: only borrower can extend
    if (!loan.getUsername().equals(username))
    {
      throw new IllegalStateException("User is not the owner of the loan");
    }

    // Everything valid — call gRPC
    loanGrpcService.extendLoan(loanId, username);
  }
}

