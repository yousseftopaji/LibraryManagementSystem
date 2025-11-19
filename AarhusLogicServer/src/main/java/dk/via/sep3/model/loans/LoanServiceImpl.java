package dk.via.sep3.model.loans;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOLoan;
import dk.via.sep3.DTOUser;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
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
    validateUser(createLoanDTO.getUsername());
    DTOBook targetBook = findAvailableBook(createLoanDTO.getBookISBN());
    updateBookStatus(targetBook);
    DTOLoan grpcLoan = createGrpcLoan(createLoanDTO, targetBook);
    return convertToLoanDTO(grpcLoan);
  }

  private void validateUser(String username)
  {
    DTOUser user = userGrpcService.getUserByUsername(username);
    if (user == null)
    {
      throw new IllegalArgumentException("User not found with username: " + username);
    }
  }

  private DTOBook findAvailableBook(String isbn)
  {
    List<DTOBook> books = bookGrpcService.getBooksByIsbn(isbn);
    for (DTOBook book : books)
    {
      if (book.getState().equalsIgnoreCase("AVAILABLE"))
      {
        return book;
      }
    }
    throw new IllegalArgumentException("Book not found or not available with ISBN: " + isbn);
  }

  private void updateBookStatus(DTOBook book)
  {
    bookGrpcService.updateBookStatus(String.valueOf(book.getId()), "Borrowed");
  }

  private DTOLoan createGrpcLoan(CreateLoanDTO createLoanDTO, DTOBook book)
  {
    Date today = new Date(System.currentTimeMillis());
    Date borrowDate = Date.valueOf(today.toLocalDate());
    Date dueDate = Date.valueOf(today.toLocalDate().plusDays(30));

    DTOLoan grpcLoan = loanGrpcService.createLoan(
        createLoanDTO.getUsername(),
        String.valueOf(book.getId()),
        borrowDate.toString(),
        dueDate.toString()
    );

    if (grpcLoan == null || grpcLoan.getId() <= 0)
    {
      throw new RuntimeException("Failed to create loan - received invalid response from server");
    }
    return grpcLoan;
  }

  private LoanDTO convertToLoanDTO(DTOLoan grpcLoan)
  {
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