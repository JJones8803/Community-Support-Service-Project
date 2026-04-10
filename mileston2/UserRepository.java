package com.scholarsweserve.repository;

import com.scholarsweserve.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User persistence.
 *
 * No database is connected yet — this interface defines the contract
 * that a future JPA implementation (or in-memory stub) will fulfill.
 */
public interface UserRepository {

    // ── Create / Update ──────────────────────────────────────────────────────────

    /** Persists a new user or updates an existing one (upsert by id). */
    User save(User user);

    // ── Read ─────────────────────────────────────────────────────────────────────

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    /** Returns all users with the STUDENT role. */
    List<User> findAllStudents();

    /** Returns all users with the ORGANIZER role. */
    List<User> findAllOrganizers();

    List<User> findAll();

    // ── Delete ───────────────────────────────────────────────────────────────────

    void deleteById(Long id);

    // ── Existence Checks ─────────────────────────────────────────────────────────

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
