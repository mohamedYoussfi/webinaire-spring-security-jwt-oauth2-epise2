package net.youssfi.authservice.security.dtos;

public record ChangePasswordRequestDTO(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {
}
