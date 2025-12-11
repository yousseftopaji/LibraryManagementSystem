package dk.via.sep3.shared.loan;

public class LoanResponseDTO {
    private LoanDTO loan;
    private String username;

    public LoanResponseDTO() {}

    public LoanResponseDTO(LoanDTO loan, String username) {
        this.loan = loan;
        this.username = username;
    }

    public LoanDTO getLoan() {
        return loan;
    }

    public void setLoan(LoanDTO loan) {
        this.loan = loan;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
