package net.youssfi.authservice.security.dtos;

public record UserDetailsRequestDTO(String userId, String firstName, String lastName, String email) {
}
