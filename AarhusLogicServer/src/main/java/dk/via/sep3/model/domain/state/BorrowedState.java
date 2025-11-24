package dk.via.sep3.model.domain.state;

import dk.via.sep3.model.domain.Book;

public class BorrowedState implements BookState
{
  @Override public String getStateName()
  {
    return "Borrowed";
  }

  @Override public void reserveBook(Book book)
  {
    book.setCurrentState(new ReservedState());
  }

  @Override public void borrowBook(Book book)
  {
  }

  @Override public void returnBook(Book book)
  {
    book.setCurrentState(new AvailableState());
  }
}
