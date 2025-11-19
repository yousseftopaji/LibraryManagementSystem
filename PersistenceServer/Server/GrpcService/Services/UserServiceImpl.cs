using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;

namespace GrpcService.Services;

public class UserServiceImpl(IUserRepository userRepository) : UserService.UserServiceBase
{
    public override async Task<GetUserByUsernameResponse> GetUserByUsername(GetUserByUsernameRequest request, ServerCallContext context)
    {
        try
        {
            var user = await userRepository.GetUserAsync(request.Username);
            var response = new GetUserByUsernameResponse
            {
                User = new DTOUser { Username = user.Username },
                Success = true,
                Message = "User retrieved successfully."
            };
            return response;
        }
        catch (Exception ex)
        {
            return new GetUserByUsernameResponse
            {
                Success = false,
                Message = ex.Message
            };
        }
    }
}
