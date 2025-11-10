import java.util.ArrayList;
import java.util.List;

public class LibraryLogic {
    private List<Book> books = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();
    private int nextLoanId = 1;

    public void addBook(Book book) {
        books.add(book);
    }

    public Book getBook(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
               int borrowedCopies = 0;
                for (Loan loan : loans) {
                    if (loan.getIsbn().equals(isbn) && !loan.isReturned()) {
                        borrowedCopies++;
                    }
                }
                book.setAvailableCopies(book.getTotalCopies()- borrowedCopies);
                return book;
            }
        }
        return null;
    }

    public String createLoan(String username, String isbn) {
        Book book = getBook(isbn);
        if (book == null) {
            return "Book not found.";
        }
        if (book.getAvailableCopies() <= 0) {
            return "No available copies.  Try reserving the book.";
        }
        Loan loan = new Loan(nextLoanId++, username, isbn);
        loans.add(loan);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return "Loan created successfully for " + username + ". Due date: " + loan.getDueDate();
    }

    public String returnBook(int loadId){
        for (Loan loan : loans) {
            if (loan.getId() == loadId && !loan.isReturned()) {
                loan.setReturned(true);
                Book book = getBook(loan.getIsbn());
                if (book != null) {
                    book.setAvailableCopies(book.getAvailableCopies() + 1);
                }
                return "Book returned successfully.";
            }
        }
        return "Loan not found or already returned.";
    }
}
