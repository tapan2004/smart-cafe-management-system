package com.cafe.api.repository;

import com.cafe.api.entity.roles.Role;
import com.cafe.api.entity.roles.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByUserRole(RoleName role);
}