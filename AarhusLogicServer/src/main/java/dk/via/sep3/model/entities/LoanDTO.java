package dk.via.sep3.model.entities;

import java.time.LocalDate;

public class LoanDTO {
    private String id;
    private String username;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned;
    private int numberOfExtensions;

    public LoanDTO() {}

    public LoanDTO(String id, String username, String isbn, LocalDate borrowDate,
                   LocalDate dueDate, boolean returned, int numberOfExtensions) {
        this.id = id;
        this.username = username;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = returned;
        this.numberOfExtensions = numberOfExtensions;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }

    public int getNumberOfExtensions() { return numberOfExtensions; }
    public void setNumberOfExtensions(int numberOfExtensions) { this.numberOfExtensions = numberOfExtensions; }
}
