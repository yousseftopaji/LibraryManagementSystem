package dk.via.sep3.grpcConnection.loanGrpcService;

import dk.via.sep3.model.domain.Loan;

import java.util.List;

public interface LoanGrpcService {
    Loan createLoan(Loan loan);

    List<Loan> getLoansByISBN(String isbn);

    void extendLoan(Loan loan);

    Loan getLoanById(int bookId);

}
