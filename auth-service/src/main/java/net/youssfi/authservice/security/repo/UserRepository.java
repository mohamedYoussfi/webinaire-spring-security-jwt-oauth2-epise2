package net.youssfi.authservice.security.repo;

import net.youssfi.authservice.security.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<AppUser,String> {
    AppUser findByUsername(String username);
    AppUser findByEmail(String email);
    AppUser findByUsernameOrEmail(String username, String email);
    List<AppUser> findByUsernameContains(String keyWord);

}
