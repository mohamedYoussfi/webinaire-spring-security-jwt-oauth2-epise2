package net.youssfi.authservice.security.dtos;

public record RequestForRoleToUserDTO(
        String roleName, String username
) {
}
