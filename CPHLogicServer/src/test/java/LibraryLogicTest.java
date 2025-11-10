import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryLogicTest {

    private LibraryLogic logic;

    @BeforeEach
    void setUp() {
        logic = new LibraryLogic();
        // one title, 2 copies
        logic.addBook(new Book("978-1", "The Hobbit", "Tolkien", "Fantasy", 2, 2));
        // another title, 1 copy
        logic.addBook(new Book("978-2", "1984", "Orwell", "Dystopia", 1, 1));
    }

    @Test
    void getBook_computesAvailabilityFromLoans() {
        assertEquals(2, logic.getBook("978-1").getAvailableCopies(), "initial availability should be 2");

        // borrow once
        String out = logic.createLoan("alice", "978-1");
        assertTrue(out.startsWith("Loan created"), "loan should be created");

        assertEquals(1, logic.getBook("978-1").getAvailableCopies(), "availability should drop to 1");
    }

    @Test
    void createLoan_happyPath_and_dueDateSet() {
        String out = logic.createLoan("bob", "978-1");
        assertTrue(out.contains("Loan created for bob"), "should confirm borrower name");
        assertTrue(out.contains("Due date:"), "should include due date");
    }

    @Test
    void createLoan_conflictWhenNoCopies() {
        // 978-2 has only 1 copy
        String out1 = logic.createLoan("alice", "978-2");
        assertTrue(out1.startsWith("Loan created"), "first loan should succeed");

        String out2 = logic.createLoan("bob", "978-2");
        assertEquals("No available copies. Try reserving the book.", out2, "second loan must be blocked");
    }

    @Test
    void reserve_onUnavailableTitle_enqueuesUser() {
        // make it unavailable (only 1 copy)
        logic.createLoan("alice", "978-2");
        assertEquals(0, logic.viewQueue("978-2").size());

        String msg = logic.reserve("bob", "978-2");
        assertTrue(msg.contains("Reservation created"), "should enqueue");
        assertEquals(1, logic.viewQueue("978-2").size(), "queue size should be 1");
        assertEquals("bob", logic.viewQueue("978-2").get(0).getUsername());
        assertEquals(1, logic.viewQueue("978-2").get(0).getPosition());
    }

    @Test
    void reserve_preferBorrowWhenCopiesAvailableUnlessQueueExists() {
        // 978-1 has 2 copies and no queue
        String msg = logic.reserve("carol", "978-1");
        assertEquals("Copies are available now. Please borrow instead of reserving.", msg);
        assertEquals(0, logic.viewQueue("978-1").size());
    }

    @Test
    void borrowingBlockedByQueue_unlessFirstInLine() {
        // Make 978-1 partially borrowed so only 1 copy left
        logic.createLoan("alice", "978-1"); // now 1 copy available

        // Dave reserves; queue exists
        assertTrue(logic.reserve("dave", "978-1").startsWith("Reservation created"));

        // Carol tries to borrow while Dave is first: blocked
        String carol = logic.createLoan("carol", "978-1");
        assertTrue(carol.startsWith("This title is reserved."), "non-first borrower should be blocked");

        // Dave (first in queue) borrows: succeeds and his reservation is consumed
        String dave = logic.createLoan("dave", "978-1");
        assertTrue(dave.startsWith("Loan created"), "first in queue can borrow");
        assertEquals(0, logic.viewQueue("978-1").size(), "queue should be empty after first borrows");
    }

    @Test
    void returnAutoHandsOffToFirstReserver() {
        // 978-2 has only 1 copy: alice borrows it
        String out = logic.createLoan("alice", "978-2");
        assertTrue(out.startsWith("Loan created"));

        // Bob and Carol reserve
        logic.reserve("bob", "978-2");
        logic.reserve("carol", "978-2");
        List<Reservation> q = logic.viewQueue("978-2");
        assertEquals(2, q.size());
        assertEquals("bob", q.get(0).getUsername());
        assertEquals("carol", q.get(1).getUsername());

        // Alice returns -> auto-loan to Bob (first reserver)
        String ret = logic.returnBook(1);
        assertTrue(ret.startsWith("Book returned and immediately loaned to reserver bob"),
                "should auto-handoff to first reserver");

        // Now queue should have only Carol as first
        q = logic.viewQueue("978-2");
        assertEquals(1, q.size());
        assertEquals(1, q.get(0).getPosition());
        assertEquals("carol", q.get(0).getUsername());

        // Availability for 978-2 should remain 0 (handoff kept the copy checked out)
        assertEquals(0, logic.getBook("978-2").getAvailableCopies());
    }

    @Test
    void cancelReservation_renumbersQueue() {
        // make 978-2 unavailable, then create a small queue
        logic.createLoan("alice", "978-2");
        logic.reserve("bob", "978-2");     // pos 1
        logic.reserve("carol", "978-2");   // pos 2
        logic.reserve("dave", "978-2");    // pos 3

        assertEquals(3, logic.viewQueue("978-2").size());

        // cancel middle (carol)
        String msg = logic.cancelReservation("carol", "978-2");
        assertTrue(msg.startsWith("Reservation cancelled"), "should cancel and renumber");

        var q = logic.viewQueue("978-2");
        assertEquals(2, q.size(), "queue size should drop to 2");
        assertEquals("bob", q.get(0).getUsername());
        assertEquals(1, q.get(0).getPosition());
        assertEquals("dave", q.get(1).getUsername());
        assertEquals(2, q.get(1).getPosition());
    }

    @Test
    void duplicateReservationReturnsExistingPosition() {
        // make 978-2 unavailable
        logic.createLoan("alice", "978-2");

        String first = logic.reserve("bob", "978-2");
        assertTrue(first.startsWith("Reservation created"));

        String dup = logic.reserve("bob", "978-2");
        assertTrue(dup.startsWith("You already have a reservation for this title."),
                "duplicate reservation should not create a new one");

        assertEquals(1, logic.viewQueue("978-2").size());
    }
}
