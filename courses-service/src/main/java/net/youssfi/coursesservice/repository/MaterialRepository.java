package net.youssfi.coursesservice.repository;

import net.youssfi.coursesservice.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material,String> {
}
