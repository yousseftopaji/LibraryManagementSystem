package dk.via.sep3.application.services.loans;

import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.util.List;

@Service public class LoanServiceImpl implements LoanService
{
  private static final Logger logger = LoggerFactory.getLogger(
      LoanServiceImpl.class);
  private final BookGrpcService bookGrpcService;
  private final LoanGrpcService loanGrpcService;

  public LoanServiceImpl(BookGrpcService bookGrpcService,
      LoanGrpcService loanGrpcService)
  {
    this.bookGrpcService = bookGrpcService;
    this.loanGrpcService = loanGrpcService;
  }

  /**
   * Create a new loan (borrow a book) for a user.
   * <p>
   * This method performs several steps:
   * <ol>
   *   <li>Validates the user does not already have an active loan for the same ISBN.</li>
   *   <li>Finds an available copy of the requested ISBN.</li>
   *   <li>Creates and persists the Loan via gRPC.</li>
   *   <li>Updates the book status to "Borrowed".</li>
   * </ol>
   *
   * @param loan a Loan object containing at least the username and the book ISBN.
   * @return the persisted Loan with generated id and dates set.
   * @throws IllegalStateException    if the user already has an active loan for the ISBN.
   * @throws IllegalArgumentException if no book copies are found or no available copies exist.
   * @throws RuntimeException         if the loan could not be created by the backend service.
   */
  @Override public Loan createLoan(Loan loan)
  {
    logger.info("Creating loan for user {} and ISBN {}", loan.getUsername(), loan.getBookISBN());

    // Step 1: Validate no duplicate active loan
    validateNoDuplicateActiveLoan(loan.getUsername(), loan.getBookISBN());

    // Step 2: Find available book
    Book availableBook = findAndValidateAvailableBook(loan.getBookISBN());

    // Step 3: Create and persist loan
    Loan createdLoan = createAndPersistLoan(loan.getUsername(), availableBook);

    // Step 4: Update book status
    updateBookStatusToBorrowed(availableBook.getId());

    logger.info("Loan created successfully with ID {} for book {}",
        createdLoan.getLoanId(), availableBook.getId());

    return createdLoan;
  }

  /**
   * Extend an existing loan.
   * <p>
   * The extension process will:
   * <ol>
   *   <li>Retrieve and validate the loan exists.</li>
   *   <li>Check that the requesting user is the borrower.</li>
   *   <li>Verify timing constraints and maximum extension limits.</li>
   *   <li>Update the due date and persist the extension via gRPC.</li>
   * </ol>
   *
   * @param loan a Loan object containing the loan id, username and intended new due date (the service will compute the new due date).
   * @throws IllegalArgumentException if the loan does not exist.
   * @throws IllegalStateException    if the user is not the borrower, the request is too early, or the maximum extensions are reached.
   */
  @Override public void extendLoan(Loan loan)
  {
    logger.info("Extending loan {} for user {}", loan.getLoanId(), loan.getUsername());

    // Step 1: Retrieve and validate loan exists
    Loan existingLoan = retrieveAndValidateLoanExists(loan.getLoanId());

    // Step 2: Validate user is the borrower
    validateUserIsBorrower(existingLoan, loan.getUsername());

    // Step 3: Validate extension timing and limit
    validateExtensionEligibility(loan);

    // Step 4: Calculate new due date and update loan
    applyExtensionToLoan(loan);

    // Step 5: Persist the extension
    loanGrpcService.extendLoan(loan);

    logger.info("Loan {} successfully extended to {}", loan.getLoanId(), loan.getDueDate());
  }

  /**
   * Retrieve active loans for a given username.
   *
   * @param username the username to query active loans for.
   * @return a list of active Loan objects for the user.
   * @throws ResourceNotFoundException   if no active loans are found for the user.
   * @throws GrpcCommunicationException  if there is an error communicating with the loan backend.
   */
  @Override public List<Loan> getActiveLoansByUsername(String username)
  {
    logger.info("Fetching active loans for user {}", username);
    try
    {
      List<Loan> activeLoans = loanGrpcService.getActiveLoansByUsername(
          username);
      if (activeLoans == null || activeLoans.isEmpty())
      {
        logger.info("No active loans found for user {}", username);
        throw new ResourceNotFoundException("No active loans found for user: " + username);
      }
      return activeLoans;
    }
    catch (Exception ex)
    {
      logger.error("Error fetching active loans for user {}", username, ex);
      throw new GrpcCommunicationException("Failed to fetch active loans for user: " + username);
    }
  }

  // ==================== Create Loan Helper Methods ====================

  /**
   * Validates that the user doesn't already have an active loan for the same ISBN.
   *
   * @param username the borrower username
   * @param isbn     the ISBN to check
   * @throws IllegalStateException if the user already has an active loan for the ISBN
   */
  private void validateNoDuplicateActiveLoan(String username, String isbn)
  {
    List<Loan> existingLoans = loanGrpcService.getLoansByISBN(isbn);

    for (Loan existing : existingLoans)
    {
      if (existing.getUsername().equalsIgnoreCase(username) && !existing.isReturned())
      {
        logger.error("User {} already has an active loan for ISBN {}", username, isbn);
        throw new IllegalStateException("User already has an active loan for this book");
      }
    }
  }

  /**
   * Finds and validates that an available book exists for the given ISBN.
   *
   * @param isbn the ISBN to search for
   * @return an available Book domain object
   * @throws IllegalArgumentException if no books or no available copies are found
   */
  private Book findAndValidateAvailableBook(String isbn)
  {
    List<Book> books = bookGrpcService.getBooksByIsbn(isbn);
    logger.debug("Found {} books with ISBN {}", books.size(), isbn);

    if (books.isEmpty())
    {
      logger.error("No books found with ISBN {}", isbn);
      throw new IllegalArgumentException("No books found with the specified ISBN");
    }

    return findAvailableBook(books);
  }

  /**
   * Finds the first available book from a list.
   *
   * @param books candidate books
   * @return the first Book whose state equals "AVAILABLE"
   * @throws IllegalArgumentException if no available copy is found
   */
  private Book findAvailableBook(List<Book> books)
  {
    for (Book book : books)
    {
      if (book.getState().toString().equalsIgnoreCase("AVAILABLE"))
      {
        logger.debug("Available book found with ID: {}", book.getId());
        return book;
      }
    }

    logger.error("No available books found from {} total books", books.size());
    throw new IllegalArgumentException("No available copies of this book");
  }

  /**
   * Creates a loan object and persists it via gRPC.
   *
   * @param username borrower
   * @param book     book to borrow
   * @return the persisted Loan
   */
  private Loan createAndPersistLoan(String username, Book book)
  {
    Loan loan = createLoanObject(username, book);
    Loan persistedLoan = loanGrpcService.createLoan(loan);

    validateLoanPersistence(persistedLoan, username, book.getId());

    return persistedLoan;
  }

  /**
   * Creates a new loan domain object with proper dates.
   */
  private Loan createLoanObject(String username, Book book)
  {
    Date today = new Date(System.currentTimeMillis());
    Date borrowDate = Date.valueOf(today.toLocalDate());
    Date dueDate = Date.valueOf(today.toLocalDate().plusDays(30));

    Loan loan = new Loan();
    loan.setUsername(username);
    loan.setBookId(book.getId());
    loan.setBorrowDate(borrowDate);
    loan.setDueDate(dueDate);
    loan.setNumberOfExtensions(0);
    loan.setReturned(false);

    return loan;
  }

  /**
   * Validates that the loan was successfully persisted.
   */
  private void validateLoanPersistence(Loan loan, String username, long bookId)
  {
    if (loan == null || loan.getLoanId() <= 0)
    {
      logger.error("Failed to create loan via gRPC for user: {}, bookId: {}", username, bookId);
      throw new RuntimeException("Failed to create loan - invalid response from server");
    }
  }

  /**
   * Updates the book status to "Borrowed".
   *
   * @param bookId id of the book to update
   */
  private void updateBookStatusToBorrowed(long bookId)
  {
    bookGrpcService.updateBookStatus((int) bookId, "Borrowed");
    logger.debug("Book {} status updated to Borrowed", bookId);
  }

  // ==================== Extend Loan Helper Methods ====================

  /**
   * Retrieves a loan by ID and validates it exists.
   *
   * @param loanId id of loan
   * @return the Loan object
   * @throws IllegalArgumentException if loan not found
   */
  private Loan retrieveAndValidateLoanExists(int loanId)
  {
    Loan loan = loanGrpcService.getLoanById(loanId);

    if (loan == null)
    {
      logger.error("Loan not found with ID: {}", loanId);
      throw new IllegalArgumentException("Loan not found with ID: " + loanId);
    }

    return loan;
  }

  /**
   * Validates that the requesting user is the borrower of the loan.
   *
   * @param loan     loan to check
   * @param username requesting user
   * @throws IllegalStateException if user is not the borrower
   */
  private void validateUserIsBorrower(Loan loan, String username)
  {
    if (!loan.getUsername().equalsIgnoreCase(username))
    {
      logger.error("User {} attempted to extend loan {} belonging to {}",
          username, loan.getLoanId(), loan.getUsername());
      throw new IllegalStateException("Only the borrower can extend the loan");
    }
  }

  /**
   * Validates that the loan is eligible for extension (timing and count).
   */
  private void validateExtensionEligibility(Loan loan)
  {
    validateExtensionTiming(loan);
    validateMaxExtensionsNotReached(loan);
  }

  /**
   * Validates that extension is requested within the allowed time window.
   */
  private void validateExtensionTiming(Loan loan)
  {
    Date today = new Date(System.currentTimeMillis());
    Date allowableExtensionDate = Date.valueOf(loan.getDueDate().toLocalDate().minusDays(1));

    if (today.before(allowableExtensionDate))
    {
      logger.error("Extension attempted for loan {} before allowed date: {}",
          loan.getLoanId(), allowableExtensionDate);
      throw new IllegalStateException(
          "You can extend your loan starting from: " + allowableExtensionDate);
    }
  }

  /**
   * Validates that the loan hasn't reached maximum extensions.
   */
  private void validateMaxExtensionsNotReached(Loan loan)
  {
    final int MAX_EXTENSIONS = 12;

    if (loan.getNumberOfExtensions() >= MAX_EXTENSIONS)
    {
      logger.error("Loan {} has reached maximum extensions ({})",
          loan.getLoanId(), MAX_EXTENSIONS);
      throw new IllegalStateException(
          "Loan has reached the maximum number of extensions (" + MAX_EXTENSIONS + ")");
    }
  }

  /**
   * Applies the extension to the loan by updating due date and extension count.
   *
   * @param loan the loan to update; this method mutates the loan's due date and extension count
   */
  private void applyExtensionToLoan(Loan loan)
  {
    final int EXTENSION_DAYS = 30;

    Date newDueDate = Date.valueOf(loan.getDueDate().toLocalDate().plusDays(EXTENSION_DAYS));
    loan.setDueDate(newDueDate);
    loan.setNumberOfExtensions(loan.getNumberOfExtensions() + 1);

    logger.debug("Loan {} extended: new due date = {}, extensions = {}",
        loan.getLoanId(), newDueDate, loan.getNumberOfExtensions());
  }
}

