package com.doggyWalky.doggyWalky.member.repository;

import com.doggyWalky.doggyWalky.member.entity.Role;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    @Query(value="select r from Role r where r.roleName = :roleName")
    Optional<Role> findByRoleName(@Param("roleName")Role.RoleName roleName);

}
