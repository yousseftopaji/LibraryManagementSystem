package dk.via.sep3.shared;

import dk.via.sep3.shared.State;

public class Book
{
  long id;
  String ISBN;
  String title;
  String author;
  State state;

  public Book(long id, String ISBN, String title, String author, State state)
  {
    this.id = id;
    this.ISBN = ISBN;
    this.title = title;
    this.author = author;
    this.state = state;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getISBN()
  {
    return ISBN;
  }

  public void setISBN(String ISBN)
  {
    this.ISBN = ISBN;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    this.state = state;
  }
}
