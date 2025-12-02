package dk.via.sep3.grpcConnection.loanPersistenceService;

import dk.via.sep3.*;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service public class LoanPersistenceServiceImpl
        implements LoanPersistenceService
{
    private static final Logger logger = LoggerFactory.getLogger(
            LoanPersistenceService.class);
    private final LoanServiceGrpc.LoanServiceBlockingStub loanStub;

    public LoanPersistenceServiceImpl(ManagedChannel channel)
    {
        this.loanStub = LoanServiceGrpc.newBlockingStub(channel);
    }

    // java
    @Override public DTOLoan createLoan(String username, String bookId,
                                        String now, String dueDate)
    {
        try
        {
            int bookIdInt = parseBookId(bookId);
            CreateLoanRequest request = buildRequest(username, bookIdInt, now,
                    dueDate);

            logger.info(
                    "Sending gRPC request to create loan for user: {}, bookId: {}, dates: {} to {}",
                    username, bookId, now, dueDate);
            CreateLoanResponse response = callCreateLoan(request);
            return handleResponse(response);
        }
        catch (NumberFormatException ex)
        {
            logger.error("Invalid bookId format: {}", bookId, ex);
            return null;
        }
        catch (Exception ex)
        {
            logger.error("Error creating loan", ex);
            return null;
        }
    }

    @Override public List<DTOLoan> getLoansByISBN(String isbn)
    {
        GetLoansByISBNRequest request = GetLoansByISBNRequest
                .newBuilder()
                .setIsbn(isbn)
                .build();

        logger.info("Sending gRPC request to get loans for ISBN: {}", isbn);
        GetLoansByISBNResponse response = loanStub.getLoansByISBN(request);

        logger.info("Received {} loans for ISBN: {}", response.getLoansCount(), isbn);
        return response.getLoansList();
    }

    private int parseBookId(String bookId)
    {
        return Integer.parseInt(bookId);
    }

    private CreateLoanRequest buildRequest(String username, int bookIdInt,
                                           String now, String dueDate)
    {
        return CreateLoanRequest.newBuilder().setUsername(username)
                .setBookId(bookIdInt).setBorrowDate(now).setDueDate(dueDate).build();
    }

    private CreateLoanResponse callCreateLoan(CreateLoanRequest request)
    {
        return loanStub.createLoan(request);
    }

    private DTOLoan handleResponse(CreateLoanResponse response)
    {
        if (response.getSuccess())
        {
            logger.info("Loan created successfully: {}", response.getLoan());
            return response.getLoan();
        }
        else
        {
            logger.error("Failed to create loan: {}", response.getMessage());
            return null;
        }
    }
}