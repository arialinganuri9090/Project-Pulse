package com.projectpulse.repository;

import com.projectpulse.model.User;
import com.projectpulse.model.enums.Role;
import com.projectpulse.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByRoleOrderByLastNameAsc(Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(:firstName = '' OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName = '' OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:email = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
           "ORDER BY u.lastName ASC")
    List<User> searchByRole(@Param("role") Role role,
                            @Param("firstName") String firstName,
                            @Param("lastName") String lastName,
                            @Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(:firstName = '' OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName = '' OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:status IS NULL OR u.status = :status) " +
           "ORDER BY u.lastName ASC")
    List<User> searchInstructors(@Param("role") Role role,
                                  @Param("firstName") String firstName,
                                  @Param("lastName") String lastName,
                                  @Param("status") UserStatus status);

    List<User> findByRole(Role role);
}
