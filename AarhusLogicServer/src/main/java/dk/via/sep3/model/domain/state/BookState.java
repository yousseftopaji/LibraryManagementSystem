package dk.via.sep3.model.domain.state;

import dk.via.sep3.model.domain.Book;

public interface BookState
{
  String getStateName();
  void reserveBook(Book book);
  void borrowBook(Book book);
  void returnBook(Book book);
}
