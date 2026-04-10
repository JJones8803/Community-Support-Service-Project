package com.scholarsweserve.service;

import com.scholarsweserve.model.HourLog;
import com.scholarsweserve.model.User;
import com.scholarsweserve.repository.HourLogRepository;
import com.scholarsweserve.repository.UserRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for all user-related business logic.
 *
 * Key feature: the Weekly Target Calculator — given a student's graduation date
 * and total required hours, it tells them exactly how many hours/week they need.
 */
public class UserService {

    private final UserRepository userRepository;
    private final HourLogRepository hourLogRepository;

    public UserService(UserRepository userRepository, HourLogRepository hourLogRepository) {
        this.userRepository = userRepository;
        this.hourLogRepository = hourLogRepository;
    }

    // ── Create ───────────────────────────────────────────────────────────────────

    /**
     * Registers a new user account.
     * Validates that email and username are not already taken.
     *
     * @throws IllegalArgumentException if email or username is already in use.
     */
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + user.getEmail());
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + user.getUsername());
        }
        return userRepository.save(user);
    }

    // ── Read ─────────────────────────────────────────────────────────────────────

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllStudents() {
        return userRepository.findAllStudents();
    }

    // ── Update ───────────────────────────────────────────────────────────────────

    /**
     * Updates a student's graduation goal (date + required hours).
     * This recalculates their weekly target automatically.
     */
    public User updateGoal(Long userId, LocalDate graduationDate, double totalHoursRequired) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setGraduationDate(graduationDate);
        user.setTotalHoursRequired(totalHoursRequired);
        return userRepository.save(user);
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ── Goal Dashboard Logic ─────────────────────────────────────────────────────

    /**
     * Returns the student's total approved volunteer hours.
     * This drives the "Current Hours" side of the progress bar.
     */
    public double getApprovedHours(Long userId) {
        return hourLogRepository.sumApprovedHoursByUserId(userId);
    }

    /**
     * Calculates how many hours per week a student must volunteer
     * to meet their graduation deadline.
     *
     * Formula:
     *   remainingHours = totalRequired - approvedHours
     *   weeksRemaining = days until graduation / 7
     *   weeklyTarget   = remainingHours / weeksRemaining
     *
     * Returns 0.0 if the goal is already met, or a large sentinel (999)
     * if graduation has already passed.
     *
     * @param userId the student whose pace to calculate
     * @return hours per week needed, rounded to one decimal place
     */
    public double calculateWeeklyTarget(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        LocalDate today = LocalDate.now();
        LocalDate gradDate = user.getGraduationDate();

        if (gradDate == null) {
            return 0.0; // no goal set yet
        }

        if (!today.isBefore(gradDate)) {
            return 999.0; // deadline has passed — surface an urgent warning in the UI
        }

        double approvedHours = hourLogRepository.sumApprovedHoursByUserId(userId);
        double remaining = user.getTotalHoursRequired() - approvedHours;

        if (remaining <= 0) {
            return 0.0; // goal already met — student is done!
        }

        long daysRemaining = ChronoUnit.DAYS.between(today, gradDate);
        double weeksRemaining = daysRemaining / 7.0;

        double weeklyTarget = remaining / weeksRemaining;

        // Round to one decimal for display (e.g. 3.5 hours/week)
        return Math.round(weeklyTarget * 10.0) / 10.0;
    }

    /**
     * Returns a summary snapshot for the Goal Dashboard:
     *   - currentHours (approved)
     *   - goalHours (required)
     *   - weeklyTarget
     *   - percentComplete
     */
    public GoalSummary getGoalSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        double approved = getApprovedHours(userId);
        double required = user.getTotalHoursRequired();
        double percent = required > 0 ? Math.min(100.0, (approved / required) * 100.0) : 0.0;
        double weekly = calculateWeeklyTarget(userId);

        return new GoalSummary(approved, required, Math.round(percent * 10) / 10.0, weekly);
    }

    // ── Inner DTO ────────────────────────────────────────────────────────────────

    /** Lightweight value object returned to the frontend dashboard. */
    public static class GoalSummary {
        private final double currentHours;
        private final double goalHours;
        private final double percentComplete;
        private final double weeklyTargetHours;

        public GoalSummary(double currentHours, double goalHours,
                           double percentComplete, double weeklyTargetHours) {
            this.currentHours = currentHours;
            this.goalHours = goalHours;
            this.percentComplete = percentComplete;
            this.weeklyTargetHours = weeklyTargetHours;
        }

        public double getCurrentHours() { return currentHours; }
        public double getGoalHours() { return goalHours; }
        public double getPercentComplete() { return percentComplete; }
        public double getWeeklyTargetHours() { return weeklyTargetHours; }
    }
}
