using System;
using System.Text.Json;
using DTOs.Reservation;


namespace BlazorApp.Services.ReservationService;
public class HttpReservationService : IReservationService
{
    private readonly HttpClient client;

    
    private readonly AuthProvider authProvider;
    public HttpReservationService(HttpClient  client, AuthProvider authProvider)
    {
        this.client = client;
        this.authProvider = authProvider;
    }
    

    public async Task<ReservationDTO> ReserveBookAsync(CreateReservationDTO createReservationDto)
    {authProvider.AttachToken(client);
        HttpResponseMessage httpResponse = await client.PostAsJsonAsync("reservations", createReservationDto);
        string response = await httpResponse.Content.ReadAsStringAsync();

        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception($"Error creating reservation: {response}");
        }

         return JsonSerializer.Deserialize<ReservationDTO>(response, new JsonSerializerOptions { PropertyNameCaseInsensitive = true })!;
    }
}