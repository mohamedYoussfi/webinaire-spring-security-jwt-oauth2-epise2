package net.youssfi.coursesservice.repository;

import net.youssfi.coursesservice.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    List<Subscription> findByStudentUserID(String userId);
    List<Subscription> findByCourseId(String courseId);
}
