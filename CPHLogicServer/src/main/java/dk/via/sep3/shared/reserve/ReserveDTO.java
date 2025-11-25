package dk.via.sep3.shared.reserve;

public class ReserveDTO {
    private String id;
    private String username;
    private String bookId;
    private String reserveDate;
    private int queueNumber;

    public ReserveDTO()
    {
    }

    public ReserveDTO(String id, String username, String bookId, String reserveDate, int queueNumber)
    {
        this.id = id;
        this.username = username;
        this.bookId = bookId;
        this.reserveDate = reserveDate;
        this.queueNumber = queueNumber;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
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

    public String getBookId()
    {
        return bookId;
    }

    public void setBookId(String bookId)
    {
        this.bookId = bookId;
    }

    public String getReserveDate()
    {
        return reserveDate;
    }

    public void setReserveDate(String reserveDate)
    {
        this.reserveDate = reserveDate;
    }

    public int getQueueNumber()
    {
        return queueNumber;
    }

    public void setQueueNumber(int queueNumber)
    {
        this.queueNumber = queueNumber;
    }
}
