package net.youssfi.coursesservice.services;

import net.youssfi.coursesservice.entities.Student;

public interface StudentService {
    Student addNewStudent(String firstName, String lastName, String userId);
}
