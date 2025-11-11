package dk.via.sep3.model.entities;



public class CreateReservationRequest {

    private String username;


    private String isbn;

    public CreateReservationRequest() {}

    public CreateReservationRequest(String username, String isbn) {
        this.username = username;
        this.isbn = isbn;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
