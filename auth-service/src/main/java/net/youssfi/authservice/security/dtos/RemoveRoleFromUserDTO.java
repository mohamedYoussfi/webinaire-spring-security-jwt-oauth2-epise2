package net.youssfi.authservice.security.dtos;

public record RemoveRoleFromUserDTO(
        String roleName, String username
) {
}
