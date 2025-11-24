package dk.via.sep3.model.domain.state;

import dk.via.sep3.model.domain.Book;

public class ReservedState implements BookState
{
  @Override public String getStateName()
  {
    return "Reserved";
  }

  @Override public void reserveBook(Book book)
  {
  }

  @Override public void borrowBook(Book book)
  {
  }

  @Override public void returnBook(Book book)
  {
    book.setCurrentState(new BorrowedState());
  }
}
