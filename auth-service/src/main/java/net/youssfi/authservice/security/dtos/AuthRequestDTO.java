package net.youssfi.authservice.security.dtos;

public record AuthRequestDTO (
   String grantType, String username, String password, boolean withRefreshToken, String refreshToken
){ }
