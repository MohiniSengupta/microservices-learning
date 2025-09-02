package com.mohini.userservice.repository;

import com.mohini.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA automatically implements these methods based on method names
    
    /**
     * Find user by email address
     * Method name convention: findBy + FieldName
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by username
     * Method name convention: findBy + FieldName
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find users by first name (case-insensitive)
     * Method name convention: findBy + FieldName + IgnoreCase
     */
    List<User> findByFirstNameIgnoreCase(String firstName);
    
    /**
     * Find users by last name (case-insensitive)
     * Method name convention: findBy + FieldName + IgnoreCase
     */
    List<User> findByLastNameIgnoreCase(String lastName);
    
    /**
     * Check if user exists by email
     * Method name convention: existsBy + FieldName
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if user exists by username
     * Method name convention: existsBy + FieldName
     */
    boolean existsByUsername(String username);
    
    /**
     * Find users created after a specific date
     * Custom query using @Query annotation
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);
    
    /**
     * Count users by first name
     * Method name convention: countBy + FieldName
     */
    long countByFirstName(String firstName);
    
    /**
     * Find users with username containing a pattern
     * Method name convention: findBy + FieldName + Containing
     */
    List<User> findByUsernameContaining(String usernamePattern);
    
    /**
     * Find users by email domain
     * Custom query using @Query annotation
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
    List<User> findUsersByEmailDomain(@Param("domain") String domain);
}
