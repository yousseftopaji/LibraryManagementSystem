namespace RepositoryContracts;

public interface IReserveRepository
{
    Task<Entities.Reserve> CreateReserveAsync(Entities.Reserve reserve);
}