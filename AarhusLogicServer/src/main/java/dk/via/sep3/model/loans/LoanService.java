package dk.via.sep3.model.loans;

import dk.via.sep3.model.domain.Loan;
import java.util.List;

/**
 * Service interface for loan operations (create, extend, query).
 *
 * <p>Implementations are responsible for enforcing loan business rules such as extension
 * limits and due date calculations.
 */
public interface LoanService
{
  /**
   * Create and persist a new loan for the given domain object.
   *
   * @param loan domain loan containing user and book information; must not be null
   * @return persisted {@link Loan} with id and dates populated
   */
  Loan createLoan(Loan loan);

  /**
   * Extend an existing loan according to business rules (ownership, timing, max extensions).
   *
   * @param loan domain loan containing at least the loan id and requester username
   */
  void extendLoan(Loan loan);

  /**
   * Retrieve active (non-returned) loans for the specified username.
   *
   * @param username the borrower username; must not be null
   * @return list of active {@link Loan} objects
   */
  List<Loan> getActiveLoansByUsername(String username);
}
