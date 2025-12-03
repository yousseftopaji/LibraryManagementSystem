package dk.via.sep3.model.domain;

import java.sql.Date;

public class Reservation
{
  private  int id;
  private User user;
  private Book book;
  private Date reservationDate;
  private int positionInQueue;

  public Reservation()
  {
  }

  public Reservation(int id, User user, Book book, Date reservationDate, int positionInQueue)
  {
    this.id = id;
    this.user = user;
    this.book = book;
    this.reservationDate = reservationDate;
    this.positionInQueue = positionInQueue;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public User getUser()
  {
    return user;
  }

  public void setUser(User user)
  {
    this.user = user;
  }

  public Book getBook()
  {
    return book;
  }

  public void setBook(Book book)
  {
    this.book = book;
  }

  public Date getReservationDate()
  {
    return reservationDate;
  }

  public void setReservationDate(Date reservationDate)
  {
    this.reservationDate = reservationDate;
  }

  public int getPositionInQueue()
  {
    return positionInQueue;
  }

  public void setPositionInQueue(int positionInQueue)
  {
    this.positionInQueue = positionInQueue;
  }
}
