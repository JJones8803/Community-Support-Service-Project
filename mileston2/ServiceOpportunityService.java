package com.scholarsweserve.service;

import com.scholarsweserve.model.Registration;
import com.scholarsweserve.model.ServiceOpportunity;
import com.scholarsweserve.repository.RegistrationRepository;
import com.scholarsweserve.repository.ServiceOpportunityRepository;
import com.scholarsweserve.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for volunteer opportunity discovery and registration.
 *
 * This is where the Volunteer.gov API data enters the system:
 * the backend fetches listings, applies the youth-friendly filter,
 * and stores them so the frontend can display them.
 */
public class ServiceOpportunityService {

    private final ServiceOpportunityRepository opportunityRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public ServiceOpportunityService(ServiceOpportunityRepository opportunityRepository,
                                     RegistrationRepository registrationRepository,
                                     UserRepository userRepository) {
        this.opportunityRepository = opportunityRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    // ── Create ───────────────────────────────────────────────────────────────────

    /**
     * Saves a new opportunity (either API-sourced or organizer-created).
     * Automatically sets spotsAvailable = totalSpots on first save.
     */
    public ServiceOpportunity createOpportunity(ServiceOpportunity opportunity) {
        if (opportunity.getSpotsAvailable() == 0) {
            opportunity.setSpotsAvailable(opportunity.getTotalSpots());
        }
        return opportunityRepository.save(opportunity);
    }

    // ── Read ─────────────────────────────────────────────────────────────────────

    public Optional<ServiceOpportunity> getOpportunityById(Long id) {
        return opportunityRepository.findById(id);
    }

    /**
     * Returns the filtered list shown to students:
     * only youth-friendly opportunities with available spots.
     */
    public List<ServiceOpportunity> getYouthFriendlyOpportunities() {
        return opportunityRepository.findAllYouthFriendly();
    }

    public List<ServiceOpportunity> searchOpportunities(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return opportunityRepository.findAllYouthFriendly();
        }
        return opportunityRepository.findByKeyword(keyword.trim());
    }

    /** Returns the organizer's own listings for their management dashboard. */
    public List<ServiceOpportunity> getOpportunitiesByOrganizer(Long organizerId) {
        return opportunityRepository.findByOrganizerId(organizerId);
    }

    // ── Update ───────────────────────────────────────────────────────────────────

    public ServiceOpportunity updateOpportunity(ServiceOpportunity updated) {
        opportunityRepository.findById(updated.getId())
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + updated.getId()));
        return opportunityRepository.save(updated);
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    public void deleteOpportunity(Long id) {
        opportunityRepository.deleteById(id);
    }

    // ── Registration Flow ─────────────────────────────────────────────────────────

    /**
     * Registers a student for an opportunity.
     *
     * Guards:
     *  - Prevents duplicate registrations.
     *  - Prevents registration when no spots remain.
     *  - Decrements spotsAvailable atomically after a successful sign-up.
     *
     * @return the saved Registration record
     */
    public Registration registerStudent(Long userId, Long opportunityId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        ServiceOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));

        if (registrationRepository.existsByUserIdAndOpportunityId(userId, opportunityId)) {
            throw new IllegalStateException("Student is already registered for this opportunity.");
        }

        if (!opportunity.hasAvailableSpots()) {
            throw new IllegalStateException("No spots remaining for this opportunity.");
        }

        // Decrement the counter and persist
        opportunity.setSpotsAvailable(opportunity.getSpotsAvailable() - 1);
        opportunityRepository.save(opportunity);

        Registration registration = new Registration(userId, opportunityId);
        return registrationRepository.save(registration);
    }

    /**
     * Cancels a registration and returns the spot to the pool.
     */
    public void cancelRegistration(Long userId, Long opportunityId) {
        Registration reg = registrationRepository.findByOpportunityId(opportunityId)
                .stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Registration not found."));

        // Return the spot
        opportunityRepository.findById(opportunityId).ifPresent(opp -> {
            opp.setSpotsAvailable(opp.getSpotsAvailable() + 1);
            opportunityRepository.save(opp);
        });

        registrationRepository.deleteById(reg.getId());
    }

    /** Returns the full roster for an organizer's opportunity. */
    public List<Registration> getRosterForOpportunity(Long opportunityId) {
        return registrationRepository.findByOpportunityId(opportunityId);
    }

    // ── Volunteer.gov API Integration Hook ────────────────────────────────────────

    /**
     * Applies youth-friendly filtering logic to a raw API listing.
     *
     * Current rules (expand as the API's age-eligibility fields become clearer):
     *  - Title/description must NOT contain "adult only", "18+", or "21+"
     *  - Falls back to marking as youth-friendly when age info is absent
     *    (conservative inclusion — better to show and let students check)
     *
     * @param rawTitle the job title from the API
     * @param rawDescription the description from the API
     * @return true when the opportunity should be shown to high school students
     */
    public boolean isYouthEligible(String rawTitle, String rawDescription) {
        String combined = ((rawTitle == null ? "" : rawTitle)
                + " " + (rawDescription == null ? "" : rawDescription)).toLowerCase();
        return !combined.contains("adult only")
                && !combined.contains("18+")
                && !combined.contains("21+")
                && !combined.contains("must be 18")
                && !combined.contains("must be 21");
    }
}
