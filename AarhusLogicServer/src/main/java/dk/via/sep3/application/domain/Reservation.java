package dk.via.sep3.application.domain;

import java.sql.Date;

public class Reservation
{
  private  int id;
  private String username;
  private int bookId;
  private String bookISBN;
  private Date reservationDate;
  private int positionInQueue;

  public Reservation()
  {
  }

  public Reservation(int id, String username, int bookId, Date reservationDate, int positionInQueue)
  {
    this.id = id;
    this.username = username;
    this.bookId = bookId;
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

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public int getBookId()
  {
    return bookId;
  }

  public void setBookId(int bookId)
  {
    this.bookId = bookId;
  }

  public String getBookISBN()
  {
    return bookISBN;
  }

  public void setBookISBN(String bookISBN)
  {
    this.bookISBN = bookISBN;
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
