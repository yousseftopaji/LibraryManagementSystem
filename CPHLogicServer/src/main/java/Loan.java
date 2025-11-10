import java.time.LocalDate;
public class Loan {

   private int id;
   private String username;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean isReturned;

    public Loan(int id, String username, String isbn) {
        this.id = id;
        this.username = username;
        this.isbn = isbn;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(30);
        this.isReturned = false;
    }

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getIsbn() {
        return isbn;
    }
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

}
