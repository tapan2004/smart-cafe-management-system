package com.cafe.api.repository;

import com.cafe.api.dto.response.UserResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cafe.api.entity.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(@Param("email") String email);

    @Query("""
            SELECT new com.cafe.api.dto.response.UserResponseDTO(
            u.id,u.name,u.contactNumber,u.email,u.status,
            CAST(r.userRole AS string))
            FROM User u JOIN u.roles r
            WHERE r.userRole='ROLE_STAFF'
            """)
    List<UserResponseDTO> getAllUsers();

    @Query("""
            SELECT u.email
            FROM User u JOIN u.roles r
            WHERE r.userRole='ROLE_ADMIN'
            """)
    List<String> getAllAdminEmails();
}