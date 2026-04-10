package com.scholarsweserve.repository;

import com.scholarsweserve.model.HourLog;
import com.scholarsweserve.model.HourLog.LogStatus;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for HourLog persistence.
 */
public interface HourLogRepository {

    // ── Create / Update ──────────────────────────────────────────────────────────

    HourLog save(HourLog hourLog);

    // ── Read ─────────────────────────────────────────────────────────────────────

    Optional<HourLog> findById(Long id);

    /** All logs belonging to a single student. */
    List<HourLog> findByUserId(Long userId);

    /** Logs filtered by status — useful for organizer approval queues. */
    List<HourLog> findByUserIdAndStatus(Long userId, LogStatus status);

    /** All PENDING logs (organizer review dashboard). */
    List<HourLog> findAllPending();

    // ── Aggregates ───────────────────────────────────────────────────────────────

    /** Sum of APPROVED hours for a student — drives the progress bar. */
    double sumApprovedHoursByUserId(Long userId);

    // ── Delete ───────────────────────────────────────────────────────────────────

    void deleteById(Long id);
}
