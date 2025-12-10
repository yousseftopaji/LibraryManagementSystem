public class JwtAuthMessageHandler : DelegatingHandler
{
    private readonly AuthProvider authProvider;

    public JwtAuthMessageHandler(AuthProvider authProvider)
    {
        this.authProvider = authProvider;
    }

    protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)
    {
        var token = authProvider.GetToken();
        if (!string.IsNullOrEmpty(token))
        {
            request.Headers.Authorization = new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", token);
        }

        return await base.SendAsync(request, cancellationToken);
    }
}
