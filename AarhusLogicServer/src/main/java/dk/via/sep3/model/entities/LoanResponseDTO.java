package dk.via.sep3.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoanResponseDTO
{
  @JsonProperty("loanId")
  private String loanId;

  @JsonProperty("bookId")
  private String bookId;

  @JsonProperty("isbn")
  private String isbn;

  @JsonProperty("userId")
  private String userId;

  @JsonProperty("loanDate")
  private String loanDate;

  @JsonProperty("dueDate")
  private String dueDate;

  public LoanResponseDTO()
  {
  }

  public LoanResponseDTO(String loanId, String bookId, String isbn, String userId, String loanDate, String dueDate)
  {
    this.loanId = loanId;
    this.bookId = bookId;
    this.isbn = isbn;
    this.userId = userId;
    this.loanDate = loanDate;
    this.dueDate = dueDate;
  }

  public String getLoanId()
  {
    return loanId;
  }

  public void setLoanId(String loanId)
  {
    this.loanId = loanId;
  }

  public String getBookId()
  {
    return bookId;
  }

  public void setBookId(String bookId)
  {
    this.bookId = bookId;
  }

  public String getIsbn()
  {
    return isbn;
  }

  public void setIsbn(String isbn)
  {
    this.isbn = isbn;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getLoanDate()
  {
    return loanDate;
  }

  public void setLoanDate(String loanDate)
  {
    this.loanDate = loanDate;
  }

  public String getDueDate()
  {
    return dueDate;
  }

  public void setDueDate(String dueDate)
  {
    this.dueDate = dueDate;
  }
}

