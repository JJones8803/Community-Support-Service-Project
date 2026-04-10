package com.scholarsweserve.service;

import com.scholarsweserve.model.HourLog;
import com.scholarsweserve.model.HourLog.LogStatus;
import com.scholarsweserve.repository.HourLogRepository;
import com.scholarsweserve.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for hour logging and organizer approval workflows.
 */
public class HourLogService {

    private final HourLogRepository hourLogRepository;
    private final UserRepository userRepository;

    public HourLogService(HourLogRepository hourLogRepository, UserRepository userRepository) {
        this.hourLogRepository = hourLogRepository;
        this.userRepository = userRepository;
    }

    // ── Create ───────────────────────────────────────────────────────────────────

    /**
     * Submits a new hour log entry for a student.
     * Validates that hoursEarned is positive and the user exists.
     */
    public HourLog logHours(Long userId, Long opportunityId, double hoursEarned, LocalDate dateCompleted) {
        if (hoursEarned <= 0) {
            throw new IllegalArgumentException("Hours earned must be greater than zero.");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        HourLog log = new HourLog(userId, opportunityId, hoursEarned, dateCompleted);
        return hourLogRepository.save(log);
    }

    // ── Read ─────────────────────────────────────────────────────────────────────

    public Optional<HourLog> getLogById(Long id) {
        return hourLogRepository.findById(id);
    }

    public List<HourLog> getLogsByUser(Long userId) {
        return hourLogRepository.findByUserId(userId);
    }

    public List<HourLog> getPendingLogs() {
        return hourLogRepository.findAllPending();
    }

    // ── Update / Approve / Reject ─────────────────────────────────────────────────

    /**
     * Approves a pending hour log.
     * Once approved, the hours count toward the student's progress bar.
     */
    public HourLog approveLog(Long logId) {
        HourLog log = hourLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Log not found: " + logId));

        if (log.getStatus() != LogStatus.PENDING) {
            throw new IllegalStateException("Only PENDING logs can be approved.");
        }

        log.setStatus(LogStatus.APPROVED);
        return hourLogRepository.save(log);
    }

    /**
     * Rejects a pending hour log (e.g. suspected duplicate or invalid submission).
     */
    public HourLog rejectLog(Long logId) {
        HourLog log = hourLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Log not found: " + logId));

        log.setStatus(LogStatus.REJECTED);
        return hourLogRepository.save(log);
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    public void deleteLog(Long id) {
        hourLogRepository.deleteById(id);
    }
}
