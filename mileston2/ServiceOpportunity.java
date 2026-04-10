package com.scholarsweserve.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a volunteer opportunity fetched from the Volunteer.gov API
 * or created by a local organizer.
 *
 * The isYouthFriendly flag is set by the backend filter — not trusted from
 * the raw API — so that age-appropriate filtering is consistent.
 */
public class ServiceOpportunity {

    private Long id;
    private String title;
    private String organization;
    private String description;
    private String address;
    private String city;
    private String state;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    /** Computed hours for a single session of this opportunity. */
    private double sessionHours;

    /** True when backend filter confirms 14–18 age eligibility. */
    private boolean isYouthFriendly;

    /** Maximum number of volunteers allowed to register. */
    private int totalSpots;

    /** Decremented on each registration; prevents over-booking. */
    private int spotsAvailable;

    /** ID of the Organizer User who owns this listing (null for API-sourced). */
    private Long organizerId;

    /** Source tracking: "VOLUNTEER_GOV" or "LOCAL". */
    private String source;

    // Many-to-many back-reference (volunteers registered)
    private List<User> registeredVolunteers = new ArrayList<>();

    // ── Constructors ────────────────────────────────────────────────────────────

    public ServiceOpportunity() {}

    /** Convenience constructor for API-sourced opportunities. */
    public ServiceOpportunity(String title, String organization, String address,
                               LocalDateTime startDateTime, LocalDateTime endDateTime,
                               double sessionHours, boolean isYouthFriendly, int totalSpots) {
        this.title = title;
        this.organization = organization;
        this.address = address;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.sessionHours = sessionHours;
        this.isYouthFriendly = isYouthFriendly;
        this.totalSpots = totalSpots;
        this.spotsAvailable = totalSpots;
        this.source = "VOLUNTEER_GOV";
    }

    // ── Getters & Setters ────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public double getSessionHours() { return sessionHours; }
    public void setSessionHours(double sessionHours) { this.sessionHours = sessionHours; }

    public boolean isYouthFriendly() { return isYouthFriendly; }
    public void setYouthFriendly(boolean youthFriendly) { isYouthFriendly = youthFriendly; }

    public int getTotalSpots() { return totalSpots; }
    public void setTotalSpots(int totalSpots) { this.totalSpots = totalSpots; }

    public int getSpotsAvailable() { return spotsAvailable; }
    public void setSpotsAvailable(int spotsAvailable) { this.spotsAvailable = spotsAvailable; }

    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public List<User> getRegisteredVolunteers() { return registeredVolunteers; }
    public void setRegisteredVolunteers(List<User> registeredVolunteers) {
        this.registeredVolunteers = registeredVolunteers;
    }

    /** Convenience: true when at least one spot is left. */
    public boolean hasAvailableSpots() {
        return spotsAvailable > 0;
    }

    @Override
    public String toString() {
        return "ServiceOpportunity{id=" + id + ", title='" + title
                + "', spotsAvailable=" + spotsAvailable + "}";
    }
}
