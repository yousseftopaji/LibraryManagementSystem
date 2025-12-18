package dk.via.sep3.application.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

  private Book book;

  @BeforeEach
  void setup() {
    book = new Book(
        "123",
        "Test Title",
        "Author",
        State.AVAILABLE,
        new ArrayList<>()
    );
  }

  @Test
  void isAvailable_returnsTrue_whenStateIsAvailable() {
    assertTrue(book.isAvailable());
  }

  @Test
  void isAvailable_returnsFalse_whenStateIsBorrowed() {
    book.setState(State.BORROWED);
    assertFalse(book.isAvailable());
  }
}
