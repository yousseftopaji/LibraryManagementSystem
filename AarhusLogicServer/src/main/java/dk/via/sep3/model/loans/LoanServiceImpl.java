package dk.via.sep3.model.loans;

import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.utils.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.util.List;

@Service public class LoanServiceImpl implements LoanService
{
  private static final Logger logger = LoggerFactory.getLogger(
      LoanServiceImpl.class);
  private final BookGrpcService bookGrpcService;
  private final LoanGrpcService loanGrpcService;
  private final Validator validator;

  public LoanServiceImpl(BookGrpcService bookGrpcService,
                         LoanGrpcService loanGrpcService, @Qualifier("userValidator") Validator validator)
  {
    this.bookGrpcService = bookGrpcService;
    this.loanGrpcService = loanGrpcService;
    this.validator = validator;
  }

  @Override public Loan createLoan(Loan loan)
  {
    // Validate USER exists
    validator.validate(loan.getUsername());

    // Prevent same user borrowing same ISBN while they have an active loan
    List<Loan> existingLoansForIsbn = loanGrpcService.getLoansByISBN(
        loan.getBookISBN());
    for (Loan existing : existingLoansForIsbn)
    {
      if (existing.getUsername().equalsIgnoreCase(loan.getUsername())
          && !existing.isReturned())
      {
        logger.error("User {} already has an active loan for ISBN {}",
            loan.getUsername(), loan.getBookISBN());
        throw new IllegalStateException(
            "User already has an active loan for this book");
      }
    }

    // Validate BOOK exists and is available
    List<Book> books = bookGrpcService.getBooksByIsbn(loan.getBookISBN());
    System.out.println(
        "Books found with ISBN " + loan.getBookISBN() + ": " + books.size());

    // Find an available book from the list
    Book targetBook = bookFinder(books);

    // Create LOAN
    Loan toBeCreatedLoan = loanCreator(loan.getUsername(), targetBook);
    Loan grpcLoan = loanGrpcService.createLoan(toBeCreatedLoan);

    if (grpcLoan == null || grpcLoan.getLoanId() <= 0)
    {
      logger.error("Failed to create loan via gRPC for user: {}, bookId: {}",
          loan.getUsername(), targetBook.getId());
      throw new RuntimeException(
          "Failed to create loan - received invalid response from server");
    }

    bookGrpcService.updateBookStatus(targetBook.getId(), "Borrowed");

    logger.info("createLoan called for book: {}", targetBook.getId());

    return grpcLoan;
  }

  @Override public void extendLoan(int loanId)
  {
    // Get LOAN by loanId
    logger.info("extendLoan called for book: {}", loanId);
    Loan loan = loanGrpcService.getLoanById(loanId);
    if (loan == null)
    {
      logger.error("extendLoan called for loan: {} not found", loanId);
      throw new IllegalArgumentException("Loan not found with ID: " + loanId);
    }

    // Only borrower can extend
//    String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
//    System.out.println("Current authenticated user: " + currentUsername);
//    if (!loan.getUsername().equalsIgnoreCase(currentUsername))
//    {
//      logger.error("extendLoan called for loan: {} by user {} - not the borrower", loanId, currentUsername);
//      throw new IllegalStateException("Only the borrower can extend the loan.");
//    }

    // Check if extension is allowed only within 1 day before due date
    Date today = new Date(System.currentTimeMillis());
    Date allowableExtensionDate = Date.valueOf(
        loan.getDueDate().toLocalDate().minusDays(1));
    if (today.before(allowableExtensionDate))
    {
      logger.error(
          "extendLoan called for loan: {} - extension not allowed until {}",
          loanId, allowableExtensionDate);
      throw new IllegalStateException(
          "Sorry, you can extend your loan on: " + allowableExtensionDate
              + ", and not before that");
    }

    // Max 12 extensions
    if (loan.getNumberOfExtensions() >= 12)
    {
      logger.error("extendLoan called for loan: {} has reached max extensions",
          loanId);
      throw new IllegalStateException(
          "Loan has reached the maximum number of extensions.");
    }

    // Business rule: only borrower can extend
    //TODO: Implement authentication to get current username

    //Modify due date and number of extensions
    Date newDueDate = Date.valueOf(
        loan.getDueDate().toLocalDate().plusDays(30));
    loan.setDueDate(newDueDate);
    loan.setNumberOfExtensions(loan.getNumberOfExtensions() + 1);
    logger.info("Loan {} due date extended to {}", loanId, newDueDate);

    // Everything valid — call gRPC
    loanGrpcService.extendLoan(loan);
  }

  private Book bookFinder(List<Book> books)
  {
    Book targetBook = null;
    for (Book book : books)
    {
      if (book.getState().toString().equalsIgnoreCase("AVAILABLE"))
      {
        targetBook = book;
        logger.debug("Book found: {}", targetBook.getId());
        break;
      }
    }

    if (targetBook == null)
    {
      logger.error("createLoan called - book not found");
      throw new IllegalArgumentException("Book not found");
    }
    return targetBook;
  }

  private Loan loanCreator(String username, Book targetBook)
  {
    Date today = new Date(System.currentTimeMillis());
    Date borrowDate = Date.valueOf(today.toLocalDate());
    Date dueDate = Date.valueOf(today.toLocalDate().plusDays(30));

    Loan toBeCreatedLoan = new Loan();
    toBeCreatedLoan.setUsername(username);
    toBeCreatedLoan.setBookId(targetBook.getId());
    toBeCreatedLoan.setBorrowDate(borrowDate);
    toBeCreatedLoan.setDueDate(dueDate);

    return toBeCreatedLoan;
  }
}