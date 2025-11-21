using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;
using Entities;
using System.Globalization;

namespace GrpcService.Services;

public class ReserveServiceImpl(IReserveRepository reserveRepository) : ReserveService.ReserveServiceBase
{
    public override async Task<CreateReserveResponse> ReserveService(global::GrpcService.Protos.CreateReserveRequest request, ServerCallContext context)
    {
        var response = new global::GrpcService.Protos.CreateReserveResponse();
        try
        {
            var reserve = new Reserve
            {
                ReserveDate = DateTime.Parse(request.ReserveDate, CultureInfo.InvariantCulture),
                Username = request.Username,
                BookId = request.BookId
            };

            var createdReserve = await reserveRepository.CreateReserveAsync(reserve);

            response.Reserve = new global::GrpcService.Protos.DTOReserve
            {
                Id = createdReserve.Id,
                ReserveDate = createdReserve.ReserveDate.ToString("yyyy-MM-dd"),
                Username = createdReserve.Username,
                BookId = createdReserve.BookId
            };
            response.Success = true;
            response.Message = "Reserve created successfully.";
        }
        catch (Exception ex)
        {
            response.Reserve = null;
            response.Success = false;
            response.Message = $"Error creating reserve: {ex.Message}";
        }

        return response;
    }
}