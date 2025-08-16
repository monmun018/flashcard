package com.app.flashcard.user.repository;

import com.app.flashcard.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Legacy methods (for backward compatibility)
    List<User> findByUserLoginID(String userLoginID);
    
    // Modern methods
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    // Flexible login: find by username OR email OR legacy userLoginID
    @Query("SELECT u FROM User u WHERE u.username = :loginIdentifier OR u.email = :loginIdentifier OR u.userLoginID = :loginIdentifier")
    Optional<User> findByLoginIdentifier(@Param("loginIdentifier") String loginIdentifier);
    
    // Check existence methods
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUserLoginID(String userLoginID);
    
    // Combined existence check for flexible registration
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :identifier OR u.email = :identifier OR u.userLoginID = :identifier")
    boolean existsByAnyLoginIdentifier(@Param("identifier") String identifier);
}