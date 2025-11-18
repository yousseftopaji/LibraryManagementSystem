package dk.via.sep3.shared;

public class CreateLoanDTO {
    private String username;
    private String bookId;

    public CreateLoanDTO() {
    }

    public CreateLoanDTO(String username, String bookId) {
        this.username = username;
        this.bookId = bookId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
