using System;
using System.Text.Json;
using DTOs.Reservation;


namespace BlazorApp.Services.ReservationService;
public class HttpReservationService : IReservationService
{
    private readonly HttpClient client;

    public HttpReservationService(HttpClient client)
    {
        this.client = client;
    }

    public async Task<ReservationDTO> ReserveBookAsync(CreateReservationDTO createReservationDto)
    {
        HttpResponseMessage httpResponse = await client.PostAsJsonAsync("reservations", createReservationDto);
        string response = await httpResponse.Content.ReadAsStringAsync();

        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception($"Error creating reservation: {response}");
        }

         return JsonSerializer.Deserialize<ReservationDTO>(response, new JsonSerializerOptions { PropertyNameCaseInsensitive = true })!;
    }
}