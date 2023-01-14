package net.youssfi.authservice.security.dtos;

public record AddRoleToUserDTO(
        String roleName, String username, boolean deleteRequestRole
) {
}
