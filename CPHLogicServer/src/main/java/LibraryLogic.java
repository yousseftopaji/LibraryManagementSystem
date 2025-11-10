import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryLogic {
    private List<Book> books = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    private int nextLoanId = 1;
    private int nextReservationId = 1;


    public void addBook(Book book) { books.add(book); }

    public Book getBook(String isbn) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                int borrowed = 0;
                for (Loan l : loans) {
                    if (l.getIsbn().equals(isbn) && !l.isReturned()) borrowed++;
                }
                b.setAvailableCopies(b.getTotalCopies() - borrowed);
                return b;
            }
        }
        return null;
    }


    public String createLoan(String username, String isbn) {
        Book book = getBook(isbn);
        if (book == null) return "Book not found.";

        if (book.getAvailableCopies() <= 0) {
            return "No available copies. Try reserving the book.";
        }
        Reservation first = peekFirstReservation(isbn);
        if (first != null && !first.getUsername().equals(username)) {
            return "This title is reserved. Only the first reserver may borrow now (current first: "
                    + first.getUsername() + ").";
        }


        Loan loan = new Loan(nextLoanId++, username, isbn);
        loans.add(loan);
        book.setAvailableCopies(book.getAvailableCopies() - 1);


        if (first != null && first.getUsername().equals(username)) {
            removeFirstReservation(isbn); // re-numbers queue
        }

        return "Loan created for " + username + ". Due date: " + loan.getDueDate();
    }


    public String returnBook(int loanId) {
        for (Loan l : loans) {
            if (l.getId() == loanId && !l.isReturned()) {
                l.setReturned(true);

                Book book = getBook(l.getIsbn());
                if (book == null) return "Book not found during return.";

                Reservation first = peekFirstReservation(l.getIsbn());
                if (first == null) {

                    book.setAvailableCopies(book.getAvailableCopies() + 1);
                    return "Book returned successfully. No one was waiting.";
                } else {
                    Loan newLoan = new Loan(nextLoanId++, first.getUsername(), l.getIsbn());
                    loans.add(newLoan);
                    removeFirstReservation(l.getIsbn());

                    return "Book returned and immediately loaned to reserver " +
                            newLoan.getUsername() + ". New due date: " + newLoan.getDueDate();
                }
            }
        }
        return "Loan not found or already returned.";
    }

    public String reserve(String username, String isbn) {
        Book book = getBook(isbn);
        if (book == null) return "Book not found.";

        if (hasReservation(username, isbn)) {
            int pos = getPosition(username, isbn);
            return "You already have a reservation for this title. Your position: " + pos + ".";
        }

        Reservation first = peekFirstReservation(isbn);
        boolean queueExists = (first != null);
        boolean copiesAvailable = book.getAvailableCopies() > 0;

        if (copiesAvailable && !queueExists) {
            return "Copies are available now. Please borrow instead of reserving.";
        }

        int nextPos = queueSize(isbn) + 1;
        Reservation r = new Reservation(nextReservationId++, username, isbn, nextPos);
        reservations.add(r);

        return "Reservation created. Your position in line: " + nextPos + ".";
    }

    public String cancelReservation(String username, String isbn) {
        List<Reservation> queue = queue(isbn);
        Reservation target = null;
        for (Reservation r : queue) {
            if (r.getUsername().equals(username)) { target = r; break; }
        }
        if (target == null) return "No reservation found for this user and title.";

        reservations.remove(target);
        renumberQueue(isbn);
        return "Reservation cancelled. Queue re-numbered.";
    }

    public List<Reservation> viewQueue(String isbn) {
        return queue(isbn);
    }

    private Reservation peekFirstReservation(String isbn) {
        List<Reservation> q = queue(isbn);
        return q.isEmpty() ? null : q.get(0);
    }

    private void removeFirstReservation(String isbn) {
        List<Reservation> q = queue(isbn);
        if (!q.isEmpty()) {
            reservations.remove(q.get(0));
            renumberQueue(isbn);
        }
    }

    private int queueSize(String isbn) {
        int size = 0;
        for (Reservation r : reservations) if (r.getIsbn().equals(isbn)) size++;
        return size;
    }

    private boolean hasReservation(String username, String isbn) {
        for (Reservation r : reservations) {
            if (r.getIsbn().equals(isbn) && r.getUsername().equals(username)) return true;
        }
        return false;
    }

    private int getPosition(String username, String isbn) {
        List<Reservation> q = queue(isbn);
        for (Reservation r : q) {
            if (r.getUsername().equals(username)) return r.getPosition();
        }
        return -1;
    }

    private List<Reservation> queue(String isbn) {
        return reservations.stream()
                .filter(r -> r.getIsbn().equals(isbn))
                .sorted(Comparator.comparingInt(Reservation::getPosition)
                        .thenComparing(Reservation::getCreatedAt))
                .collect(Collectors.toList());
    }

    private void renumberQueue(String isbn) {
        List<Reservation> q = queue(isbn);
        for (int i = 0; i < q.size(); i++) {
            q.get(i).setPosition(i + 1);
        }
    }
}
