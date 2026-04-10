package com.scholarsweserve.repository;

import com.scholarsweserve.model.ServiceOpportunity;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ServiceOpportunity persistence.
 */
public interface ServiceOpportunityRepository {

    // ── Create / Update ──────────────────────────────────────────────────────────

    ServiceOpportunity save(ServiceOpportunity opportunity);

    // ── Read ─────────────────────────────────────────────────────────────────────

    Optional<ServiceOpportunity> findById(Long id);

    List<ServiceOpportunity> findAll();

    /** Returns only youth-friendly (age 14–18 eligible) opportunities. */
    List<ServiceOpportunity> findAllYouthFriendly();

    /** Keyword search across title, organization, and description. */
    List<ServiceOpportunity> findByKeyword(String keyword);

    /** Find all opportunities created by a specific organizer. */
    List<ServiceOpportunity> findByOrganizerId(Long organizerId);

    /** Find opportunities with at least one spot remaining. */
    List<ServiceOpportunity> findWithAvailableSpots();

    // ── Delete ───────────────────────────────────────────────────────────────────

    void deleteById(Long id);
}
