package dk.via.sep3.model.entities;

import java.time.LocalDateTime;

public class ReservationDTO {
    private String id;
    private String username;
    private String isbn;
    private int numberInLine;
    private LocalDateTime createdAt;

    public ReservationDTO() {}

    public ReservationDTO(String id, String username, String isbn, int numberInLine, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.isbn = isbn;
        this.numberInLine = numberInLine;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getNumberInLine() { return numberInLine; }
    public void setNumberInLine(int numberInLine) { this.numberInLine = numberInLine; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
