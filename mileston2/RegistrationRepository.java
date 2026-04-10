package com.scholarsweserve.repository;

import com.scholarsweserve.model.Registration;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for the Registration join table.
 */
public interface RegistrationRepository {

    Registration save(Registration registration);

    Optional<Registration> findById(Long id);

    /** All registrations for a given volunteer (student's sign-up history). */
    List<Registration> findByUserId(Long userId);

    /** All registrations for a given opportunity (organizer's roster). */
    List<Registration> findByOpportunityId(Long opportunityId);

    /** Check whether a specific student is already signed up. */
    boolean existsByUserIdAndOpportunityId(Long userId, Long opportunityId);

    void deleteById(Long id);
}
