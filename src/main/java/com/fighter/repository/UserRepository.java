package com.fighter.repository;

import com.fighter.entity.Role;
import com.fighter.entity.User;
import com.fighter.model.AuthResponce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(Role role);

    List<User> findAllByEnabledTrue();

    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :from AND :to")
    List<User> findUsersRegisteredBetween(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :now WHERE u.id = :id")
    void updateLastLoginAt(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
    void updateEnabledStatus(@Param("id") Long id, @Param("enabled") boolean enabled);

    @Modifying
    @Query("UPDATE User u SET u.accountNonLocked = false WHERE u.id = :id")
    void lockAccount(@Param("id") Long id);
}
