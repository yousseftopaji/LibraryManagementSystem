namespace DTOs.Extension;

public class CreateExtensionDTO(int loanId, string username)
{
    public int loanId  { get; set; } = loanId;
    public String username  { get; set; } = username;
}