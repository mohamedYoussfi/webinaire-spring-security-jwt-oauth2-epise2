package net.youssfi.coursesservice.dtos;

public record ChangePasswordRequestDTO(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {
}
