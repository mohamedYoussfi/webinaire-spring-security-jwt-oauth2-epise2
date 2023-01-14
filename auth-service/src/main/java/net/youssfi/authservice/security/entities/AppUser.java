package net.youssfi.authservice.security.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.youssfi.authservice.security.enums.AccountStatus;
import net.youssfi.authservice.security.enums.Gender;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class AppUser {
    @Id
    private String id;
    @Column(unique = true)
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(unique = true)
    private String email;
    private boolean emailVerified;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") },
            uniqueConstraints = {
             @UniqueConstraint(columnNames = {"user_id","role_id"})
            }
    )
    private List<AppRole> appRoles=new ArrayList<>();
    @ElementCollection
    private List<String> requestedRoles=new ArrayList<>();
    private String photoFileName;
    private String photoURL;
    private String temporaryActivationCode;
    private Instant temporaryActivationCodeTimeStamp;
}
