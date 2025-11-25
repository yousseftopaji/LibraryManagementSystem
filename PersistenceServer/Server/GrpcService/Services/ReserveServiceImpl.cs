using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;
using Entities;

namespace GrpcService.Services;

public class ReserveServiceImpl(IReserveRepository reserveRepository) : ReserveService.ReserveServiceBase
{
    public override async Task<CreateReserveResponse> CreateReserve(CreateReserveRequest request, ServerCallContext context)
    {
        var response = new CreateReserveResponse();

        try
        {
            var reserve = new Reserve
            {
                ReserveDate = DateTime.Parse(request.ReserveDate),
                Username = request.Username,
                BookId = request.BookId,
                Status = "Pending"
            };
            // Create reserve using repository
            var createdReserve = await reserveRepository.CreateReserveAsync(reserve);

            // Populate response
            response.Reserve = new DTOReserve
            {
                Id = createdReserve.ReserveId,
                ReserveDate = createdReserve.ReserveDate.ToString("yyyy-MM-dd"),
                Username = createdReserve.Username ?? string.Empty,
                BookId = createdReserve.BookId,
                Status = createdReserve.Status ?? string.Empty
            };
            response.Success = true;
            response.Message = "Reservation created successfully.";
        }
        catch (Exception ex)
        {
            response.Reserve = null;
            response.Success = false;
            response.Message = $"Error creating reservation: {ex.Message}";
        }

        return response;
    }
}

