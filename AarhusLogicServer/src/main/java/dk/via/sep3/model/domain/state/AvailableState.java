package dk.via.sep3.model.domain.state;

import dk.via.sep3.model.domain.Book;

public class AvailableState implements BookState
{

  @Override public String getStateName()
  {
    return "Available";
  }

  @Override public void reserveBook(Book book)
  {
  }

  @Override public void borrowBook(Book book)
  {
    book.setCurrentState(new BorrowedState());
  }

  @Override public void returnBook(Book book)
  {
  }
}
