package com.scholarsweserve.model;

import java.time.LocalDateTime;

/**
 * Join table for the many-to-many relationship between Users and ServiceOpportunities.
 * Tracks when a student registered and whether they showed up.
 */
public class Registration {

    private Long id;
    private Long userId;
    private Long opportunityId;
    private LocalDateTime registeredAt;
    private boolean attended;

    // ── Constructors ────────────────────────────────────────────────────────────

    public Registration() {}

    public Registration(Long userId, Long opportunityId) {
        this.userId = userId;
        this.opportunityId = opportunityId;
        this.registeredAt = LocalDateTime.now();
        this.attended = false;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOpportunityId() { return opportunityId; }
    public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public boolean isAttended() { return attended; }
    public void setAttended(boolean attended) { this.attended = attended; }

    @Override
    public String toString() {
        return "Registration{userId=" + userId + ", opportunityId=" + opportunityId
                + ", registeredAt=" + registeredAt + "}";
    }
}
