package com.scholarsweserve.controller;

import com.scholarsweserve.model.HourLog;
import com.scholarsweserve.service.HourLogService;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller stub for Hour Logging and Organizer Approval endpoints.
 */
// @RestController
// @RequestMapping("/api/hours")
public class HourLogController {

    private final HourLogService hourLogService;

    // @Autowired
    public HourLogController(HourLogService hourLogService) {
        this.hourLogService = hourLogService;
    }

    // POST /api/hours
    // @PostMapping
    public HourLog logHours(/* @RequestParam */ Long userId,
                             /* @RequestParam(required=false) */ Long opportunityId,
                             /* @RequestParam */ double hoursEarned,
                             /* @RequestParam */ LocalDate dateCompleted) {
        return hourLogService.logHours(userId, opportunityId, hoursEarned, dateCompleted);
    }

    // GET /api/hours?userId=
    // @GetMapping
    public List<HourLog> getUserLogs(/* @RequestParam */ Long userId) {
        return hourLogService.getLogsByUser(userId);
    }

    // GET /api/hours/pending  (organizer only)
    // @GetMapping("/pending")
    public List<HourLog> getPendingLogs() {
        return hourLogService.getPendingLogs();
    }

    // PUT /api/hours/{id}/approve
    // @PutMapping("/{id}/approve")
    public HourLog approveLog(/* @PathVariable */ Long id) {
        return hourLogService.approveLog(id);
    }

    // PUT /api/hours/{id}/reject
    // @PutMapping("/{id}/reject")
    public HourLog rejectLog(/* @PathVariable */ Long id) {
        return hourLogService.rejectLog(id);
    }
}
