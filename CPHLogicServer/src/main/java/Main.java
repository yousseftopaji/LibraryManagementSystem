import org.apache.tomcat.jni.Library;

public class Main {
    public static void main(String[] args) {
        LibraryLogic libraryLogic = new LibraryLogic();

        libraryLogic.addBook(new Book("9780123456789", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", 5, 5));
        libraryLogic.addBook(new Book("139789876543210", "1984", "George Orwell", "Dystopian", 3, 3));

        Book book = libraryLogic.getBook("9780123456789");
        System.out.println("Book Title: " + book.getTitle() + ", Available Copies: " + book.getAvailableCopies());

        String result = libraryLogic.createLoan("john_doe", "9780123456789");
        System.out.println(result);

        Book updatedBook = libraryLogic.getBook("9780123456789");
        System.out.println("After borrowing: " + updatedBook.getAvailableCopies() + " copies available." );

        System.out.println(libraryLogic.returnBook(1));
        System.out.println("After returning: " + libraryLogic.getBook("9780123456789").getAvailableCopies() + " copies available." );

    }
}
