package com.scholarsweserve.controller;

import com.scholarsweserve.model.User;
import com.scholarsweserve.service.UserService;

/**
 * REST controller stub for User and Goal Dashboard endpoints.
 *
 * Spring Boot annotations are commented out so the class compiles
 * without the full Spring dependency tree in place.
 * Uncomment when Spring Boot is wired up.
 */
// @RestController
// @RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users/register
    // @PostMapping("/register")
    public User register(/* @RequestBody */ User user) {
        return userService.createUser(user);
    }

    // GET /api/users/{id}
    // @GetMapping("/{id}")
    public User getUser(/* @PathVariable */ Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // GET /api/users/{id}/goal-summary
    // @GetMapping("/{id}/goal-summary")
    public UserService.GoalSummary getGoalSummary(/* @PathVariable */ Long id) {
        return userService.getGoalSummary(id);
    }

    // PUT /api/users/{id}/goal
    // @PutMapping("/{id}/goal")
    public User updateGoal(/* @PathVariable */ Long id,
                           /* @RequestParam */ java.time.LocalDate graduationDate,
                           /* @RequestParam */ double totalHoursRequired) {
        return userService.updateGoal(id, graduationDate, totalHoursRequired);
    }

    // DELETE /api/users/{id}
    // @DeleteMapping("/{id}")
    public void deleteUser(/* @PathVariable */ Long id) {
        userService.deleteUser(id);
    }
}
