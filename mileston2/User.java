package com.scholarsweserve.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a platform user — either a Student or an Organizer.
 * Students track volunteer hours toward graduation requirements.
 * Organizers create and manage service opportunities.
 */
public class User {

    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private UserRole role; // STUDENT or ORGANIZER

    // Student-specific fields
    private LocalDate graduationDate;
    private double totalHoursRequired;

    // Relationships
    private List<HourLog> hourLogs = new ArrayList<>();
    private List<ServiceOpportunity> registeredOpportunities = new ArrayList<>();

    // ── Constructors ────────────────────────────────────────────────────────────

    public User() {}

    /** Minimal constructor used when creating a new account. */
    public User(String username, String email, String passwordHash, UserRole role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /** Full constructor for a student with goal information. */
    public User(Long id, String username, String email, String passwordHash,
                UserRole role, LocalDate graduationDate, double totalHoursRequired) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.graduationDate = graduationDate;
        this.totalHoursRequired = totalHoursRequired;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public LocalDate getGraduationDate() { return graduationDate; }
    public void setGraduationDate(LocalDate graduationDate) { this.graduationDate = graduationDate; }

    public double getTotalHoursRequired() { return totalHoursRequired; }
    public void setTotalHoursRequired(double totalHoursRequired) { this.totalHoursRequired = totalHoursRequired; }

    public List<HourLog> getHourLogs() { return hourLogs; }
    public void setHourLogs(List<HourLog> hourLogs) { this.hourLogs = hourLogs; }

    public List<ServiceOpportunity> getRegisteredOpportunities() { return registeredOpportunities; }
    public void setRegisteredOpportunities(List<ServiceOpportunity> registeredOpportunities) {
        this.registeredOpportunities = registeredOpportunities;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role=" + role + "}";
    }

    /** Enum for the two supported user roles. */
    public enum UserRole {
        STUDENT, ORGANIZER
    }
}
