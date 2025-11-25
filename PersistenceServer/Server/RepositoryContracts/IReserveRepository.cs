using System;
using DTOs.Reserve;
using Entities;

namespace RepositoryContracts;

public interface IReserveRepository
{
    Task<ReserveDTO> CreateReserveAsync(Reserve reserve);
}

