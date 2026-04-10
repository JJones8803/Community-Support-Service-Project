package com.scholarsweserve.model;

import java.time.LocalDate;

/**
 * Records a single completed volunteer session for a Student.
 *
 * Logs start as PENDING until an Organizer (or admin) approves them,
 * at which point the hours count toward the student's goal.
 */
public class HourLog {

    private Long id;

    /** The student who completed the hours. */
    private Long userId;

    /** Optional link to the opportunity where hours were earned. */
    private Long opportunityId;

    private double hoursEarned;
    private LocalDate dateCompleted;
    private String notes;
    private LogStatus status;

    // ── Constructors ────────────────────────────────────────────────────────────

    public HourLog() {}

    /** Constructor used when a student submits a new log entry. */
    public HourLog(Long userId, Long opportunityId, double hoursEarned, LocalDate dateCompleted) {
        this.userId = userId;
        this.opportunityId = opportunityId;
        this.hoursEarned = hoursEarned;
        this.dateCompleted = dateCompleted;
        this.status = LogStatus.PENDING; // always starts as PENDING
    }

    /** Full constructor. */
    public HourLog(Long id, Long userId, Long opportunityId, double hoursEarned,
                   LocalDate dateCompleted, String notes, LogStatus status) {
        this.id = id;
        this.userId = userId;
        this.opportunityId = opportunityId;
        this.hoursEarned = hoursEarned;
        this.dateCompleted = dateCompleted;
        this.notes = notes;
        this.status = status;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOpportunityId() { return opportunityId; }
    public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }

    public double getHoursEarned() { return hoursEarned; }
    public void setHoursEarned(double hoursEarned) { this.hoursEarned = hoursEarned; }

    public LocalDate getDateCompleted() { return dateCompleted; }
    public void setDateCompleted(LocalDate dateCompleted) { this.dateCompleted = dateCompleted; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LogStatus getStatus() { return status; }
    public void setStatus(LogStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "HourLog{id=" + id + ", userId=" + userId
                + ", hoursEarned=" + hoursEarned + ", status=" + status + "}";
    }

    /** Lifecycle states for organizer verification workflow. */
    public enum LogStatus {
        PENDING,   // submitted by student, awaiting review
        APPROVED,  // organizer confirmed the hours
        REJECTED   // organizer flagged as invalid
    }
}
