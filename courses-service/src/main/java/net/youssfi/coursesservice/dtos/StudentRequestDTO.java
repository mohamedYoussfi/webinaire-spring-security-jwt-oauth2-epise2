package net.youssfi.coursesservice.dtos;

import net.youssfi.coursesservice.entities.Gender;

import java.util.Date;

public record StudentRequestDTO (
        String firstName, String lastName, String email, Date birthDate, Gender gender, String userId
){}
