package net.youssfi.authservice.security.dtos;

import net.youssfi.authservice.security.enums.Gender;

public record RegistrationRequestDTO(String username, String firstName, String lastName, String email, String password, String confirmPassword, Gender gender) {
}
