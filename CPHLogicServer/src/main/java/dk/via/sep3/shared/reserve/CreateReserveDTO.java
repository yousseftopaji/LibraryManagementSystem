package dk.via.sep3.shared.reserve;

public class CreateReserveDTO {
    private String username;
    private String bookISBN;

    public CreateReserveDTO(String username, String bookISBN)
    {
        this.username = username;
        this.bookISBN = bookISBN;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getBookISBN()
    {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN)
    {
        this.bookISBN = bookISBN;
    }

}
