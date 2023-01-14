package net.youssfi.authservice.security.repo;
import net.youssfi.authservice.security.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<AppRole,Long> {
    AppRole findByRoleName(String roleName);
}
