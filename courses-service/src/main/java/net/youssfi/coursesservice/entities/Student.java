package net.youssfi.coursesservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String userID;
    private String email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String photoFileName;
    private String photoURL;
}
