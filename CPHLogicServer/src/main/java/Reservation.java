import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private String username;
    private String isbn;
    private int position;               // 1 = first in line
    private LocalDateTime createdAt;

    public Reservation(int id, String username, String isbn, int position) {
        this.id = id;
        this.username = username;
        this.isbn = isbn;
        this.position = position;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getIsbn() { return isbn; }
    public int getPosition() { return position; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setPosition(int position) { this.position = position; }
}
