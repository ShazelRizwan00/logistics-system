package com.logistics.repository;

import com.logistics.entity.User;
import com.logistics.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for the User hierarchy (single table).
 * Spring Data resolves polymorphic queries automatically because all
 * sub-types share the same table.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Used by Spring Security's UserDetailsService to load by username. */
    Optional<User> findByContactInfo(String contactInfo);

    boolean existsByContactInfo(String contactInfo);

    /** Admin: list all users of a specific role with pagination.
     *  We use TYPE() in JPQL because `role` is a discriminator column,
     *  not a persistent mapped field. */
    @Query("SELECT u FROM User u WHERE TYPE(u) = :roleClass")
    Page<User> findByRole(@Param("roleClass") Class<?> roleClass, Pageable pageable);

    /** Convenience method mapping UserRole enum to the concrete class. */
    default Page<User> findByRole(UserRole role, Pageable pageable) {
        Class<?> clazz = switch (role) {
            case CUSTOMER       -> com.logistics.entity.Customer.class;
            case DELIVERY_AGENT -> com.logistics.entity.DeliveryAgent.class;
            case ADMIN          -> com.logistics.entity.Admin.class;
        };
        return findByRole(clazz, pageable);
    }

    /**
     * Full-text search across name and contactInfo.
     * Using LOWER() for case-insensitive matching (portable across H2 & PG).
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.contactInfo) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
