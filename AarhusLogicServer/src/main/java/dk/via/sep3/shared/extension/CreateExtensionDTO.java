package dk.via.sep3.shared.extension;

public class CreateExtensionDTO {
    private int loanId;
    private String username;

    public CreateExtensionDTO(int loanId, String username) {
        this.loanId = loanId;
        this.username = username;

    }

    public CreateExtensionDTO(){

    }

    public int getLoanId() {
        return loanId;
    }
    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
