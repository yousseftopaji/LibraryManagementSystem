using DTOs.User;
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
            UserDTO? user = await userRepository.GetUserAsync(request.Username);
            if (user == null)
            {
                return new GetUserByUsernameResponse
                {
                    Success = false,
                    Message = "User not found."
                };
            }

            var response = new GetUserByUsernameResponse
            {
                User = new DTOUser
                {
                    Username = user.Username,
                    Password = user.PasswordHash,
                    PhoneNumber = user.PhoneNumber,
                    Email = user.Email,
                    Name = user.Name,
                    Role = user.Role
                },
                Success = true,
                Message = "User retrieved successfully."
            };
            return response;
        }
        catch (Exception)
        {
            return new GetUserByUsernameResponse
            {
                Success = false,
                Message = "An internal error occurred while retrieving the user."
            };
        }
    }

    public override async Task<CreateUserResponse> CreateUser(CreateUserRequest request, ServerCallContext context)
    {
        try
        {
            var userEntity = new Entities.User
            {
                Username = request.User.Username,
                PasswordHash = request.User.Password,
                PhoneNumber = request.User.PhoneNumber,
                Email = request.User.Email,
                Name = request.User.Name,
                Role = request.User.Role
            };
            var user = await userRepository.CreateUserAsync(userEntity);
            var response = new CreateUserResponse
            {
                User = new DTOUser { Username = user.Username },
                Success = true,
                Message = "User created successfully."
            };
            return response;
        }
        catch (Exception ex)
        {
            return new CreateUserResponse
            {
                Success = false,
                Message = ex.Message
            };
        }
    }
}
