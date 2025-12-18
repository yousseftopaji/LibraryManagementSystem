using System;
using DTOs.Loan;

namespace BlazorApp.Components.ViewModels;

public class LoanViewModel
{
    public int LoanId { get; }
    public int BookId { get; }
    public DateTime DueDate { get; }
    public DateTime BorrowDate { get; }
    public int NumberOfExtensions { get; }

    public string FormattedDueDate => DueDate.ToString("yyyy-MM-dd");
    public string FormattedBorrowDate => BorrowDate.ToString("yyyy-MM-dd");

    public LoanViewModel(LoanDTO dto)
    {
        LoanId = dto.LoanId;
        BookId = dto.BookId;
        DueDate = dto.DueDate;
        BorrowDate = dto.BorrowDate;
        NumberOfExtensions = dto.NumberOfExtensions;
    }
}
