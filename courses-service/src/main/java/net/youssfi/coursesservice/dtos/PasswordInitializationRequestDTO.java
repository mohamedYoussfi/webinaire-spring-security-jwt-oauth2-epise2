package net.youssfi.coursesservice.dtos;
public record PasswordInitializationRequestDTO(
        String password, String confirmPassword,
        String authorizationCode, String email
){ }
