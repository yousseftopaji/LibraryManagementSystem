package dk.via.sep3.shared.reservation;

import java.sql.Date;

public class ReservationDTO
{
  private int id;
  private String username;
  private int bookId;
  private Date reservationDate;
  private int positionInQueue;

  public ReservationDTO(int id, String username, int bookId,
      Date reservationDate, int positionInQueue)
  {
    this.id = id;
    this.username = username;
    this.bookId = bookId;
    this.reservationDate = reservationDate;
    this.positionInQueue = positionInQueue;
  }

  public String getUsername()
  {
    return username;
  }

  public int getBookId()
  {
    return bookId;
  }

  public Date getReservationDate()
  {
    return reservationDate;
  }

  public int getPositionInQueue()
  {
    return positionInQueue;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public void setReservationDate(Date reservationDate)
  {
    this.reservationDate = reservationDate;
  }

  public void setBookId(int bookId)
  {
    this.bookId = bookId;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public void setPositionInQueue(int positionInQueue)
  {
    this.positionInQueue = positionInQueue;
  }
}