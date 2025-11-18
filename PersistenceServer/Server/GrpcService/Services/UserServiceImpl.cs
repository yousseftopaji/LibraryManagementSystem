using GrpcService.Protos;
using RepositoryContracts;

namespace GrpcService.Services;

public class UserServiceImpl(IUserRepository userRepository) : UserService.UserServiceBase
{
    public override async Task<GetUserByUsernameResponse> GetUserByUsername(GetUserByUsernameRequest request, Grpc.Core.ServerCallContext context)
    {
        var response = new GetUserByUsernameResponse();

        try
        {
            var user = await userRepository.GetUserAsync(request.Username);
            response.User.Username = user.Username;
            response.Success = true;
            response.Message = $"User with username {request.Username} retrieved successfully.";
        }
        catch (Exception e)
        {
            response.Success = false;
            response.Message = $"Error retrieving user with username {request.Username}: {e.Message}";
        }
        
        return response;
    }
}