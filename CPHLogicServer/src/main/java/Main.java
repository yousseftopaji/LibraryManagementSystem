public class Main {
    public static void main(String[] args) {
        LibraryLogic logic = new LibraryLogic();

        logic.addBook(new Book("978-1", "The Hobbit", "Tolkien", "Fantasy", 1, 1));

        System.out.println(logic.createLoan("alice", "978-1"));
        System.out.println("Available after Alice borrows: " + logic.getBook("978-1").getAvailableCopies());

        System.out.println(logic.reserve("bob", "978-1"));
        System.out.println(logic.reserve("carol", "978-1"));

        System.out.println(logic.createLoan("dave", "978-1")); // blocked by queue (bob first)


        System.out.println(logic.returnBook(1));
        System.out.println("Available after return+handoff: " + logic.getBook("978-1").getAvailableCopies());

        System.out.println(logic.createLoan("dave", "978-1"));
        System.out.println("Queue positions:");
        for (Reservation r : logic.viewQueue("978-1")) {
            System.out.println("  " + r.getPosition() + ". " + r.getUsername());
        }
        
        System.out.println(logic.createLoan("carol", "978-1"));
        System.out.println("Queue after Carol borrows: " + logic.viewQueue("978-1").size());
    }
}
