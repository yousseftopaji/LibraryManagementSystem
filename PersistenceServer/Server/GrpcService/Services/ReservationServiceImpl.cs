using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;
using Entities;

namespace GrpcService.Services;

public class ReservationServiceImpl(IReservationRepository reservationRepository) : ReservationService.ReservationServiceBase
{
    public override async Task<CreateReservationResponse> CreateReservation(CreateReservationRequest request, ServerCallContext context)
    {
        var response = new CreateReservationResponse();

        try
        {
            var reservation = new Reservation
            {
                ReservationDate = DateTime.Parse(request.ReservationDate),
                Username = request.Username,
                BookId = request.BookId
            };
            
            // Create reservation using repository
            var createdReservation = await reservationRepository.CreateReservationAsync(reservation);

            // Populate response
            response.Reservation = new DTOReservation
            {
                Id = createdReservation.ReservationId,
                ReservationDate = createdReservation.ReservationDate.ToString("yyyy-MM-dd"),
                Username = createdReservation.Username ?? string.Empty,
                BookId = createdReservation.BookId
            };
            response.Success = true;
            response.Message = "Reservation created successfully.";
        }
        catch (Exception ex)
        {
            response.Reservation = null;
            response.Success = false;
            response.Message = $"Error creating reservation: {ex.Message}";
        }

        return response;
    }

    public override async Task<GetReservationCountByIsbnResponse> GetReservationCountByIsbn(GetReservationCountByIsbnRequest request, ServerCallContext context)
    {
        var response = new GetReservationCountByIsbnResponse();

        try
        {
            var count = await reservationRepository.GetReservationCountByIsbnAsync(request.Isbn);

            response.NumberOfReservations = count;
            response.Success = true;
            response.Message = "Reservation count retrieved successfully.";
        }
        catch (Exception ex)
        {
            response.NumberOfReservations = 0;
            response.Success = false;
            response.Message = $"Error getting reservation count: {ex.Message}";
        }

        return response;
    }
}

