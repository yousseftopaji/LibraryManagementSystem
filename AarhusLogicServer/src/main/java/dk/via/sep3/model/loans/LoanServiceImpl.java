package dk.via.sep3.model.loans;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOLoan;
import dk.via.sep3.grpcConnection.bookPersistenceService.BookPersistenceService;
import dk.via.sep3.grpcConnection.loanPersistenceService.LoanPersistenceService;
import dk.via.sep3.model.utils.validation.Validator;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService
{
  private final BookPersistenceService bookPersistenceService;
  private final LoanPersistenceService loanPersistenceService;
  private final Validator validator;

  public LoanServiceImpl(BookPersistenceService bookPersistenceService, LoanPersistenceService loanPersistenceService, Validator validator)
  {
    this.bookPersistenceService = bookPersistenceService;
    this.loanPersistenceService = loanPersistenceService;
    this.validator = validator;
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

  @Override
  public void extendLoan(int bookId, int loanId)
  {
    //Get the book by its id
    DTOBook book = bookPersistenceService.getBookById(String.valueOf(bookId));
    if (book == null)
    {
      throw new IllegalArgumentException("Book not found with ID: " + bookId);
    }
    if (book.getState().equalsIgnoreCase("reserved"))
    {
      throw new IllegalStateException("Book is reserved and cannot be extended.");
    }
    if (book.getState().equalsIgnoreCase("available"))
    {
      throw new IllegalStateException("Book is available and cannot be extended.");
    }

    //Get the loan by its id
    DTOLoan loan = loanPersistenceService.getLoanById(loanId);
    if (loan == null)
    {
      throw new IllegalArgumentException("Loan not found with ID: " + loanId);
    }
    if (loan.getNumberOfExtensions() == 12)
    {
      throw new IllegalStateException("Loan has reached the maximum number of extensions.");
    }
    //Extend the loan
    loanPersistenceService.extendLoan(loanId);
  }

  private void validateUser(String username)
  {
    validator.validateUser(username);
  }

  private DTOBook findAvailableBook(String isbn)
  {
    List<DTOBook> books = bookPersistenceService.getBooksByIsbn(isbn);
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
    bookPersistenceService.updateBookStatus(String.valueOf(book.getId()), "Borrowed");
  }

  private DTOLoan createGrpcLoan(CreateLoanDTO createLoanDTO, DTOBook book)
  {
    Date today = new Date(System.currentTimeMillis());
    Date borrowDate = Date.valueOf(today.toLocalDate());
    Date dueDate = Date.valueOf(today.toLocalDate().plusDays(30));

    DTOLoan grpcLoan = loanPersistenceService.createLoan(
        createLoanDTO.getUsername(),
        book.getId(),
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