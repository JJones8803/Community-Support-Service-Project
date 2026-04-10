package com.scholarsweserve.controller;

import com.scholarsweserve.model.Registration;
import com.scholarsweserve.model.ServiceOpportunity;
import com.scholarsweserve.service.ServiceOpportunityService;

import java.util.List;

/**
 * REST controller stub for Opportunity Discovery and Registration endpoints.
 */
// @RestController
// @RequestMapping("/api/opportunities")
public class OpportunityController {

    private final ServiceOpportunityService opportunityService;

    // @Autowired
    public OpportunityController(ServiceOpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    // GET /api/opportunities?keyword=
    // @GetMapping
    public List<ServiceOpportunity> listOpportunities(/* @RequestParam(required=false) */ String keyword) {
        return opportunityService.searchOpportunities(keyword);
    }

    // GET /api/opportunities/{id}
    // @GetMapping("/{id}")
    public ServiceOpportunity getOpportunity(/* @PathVariable */ Long id) {
        return opportunityService.getOpportunityById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found"));
    }

    // POST /api/opportunities/{id}/register?userId=
    // @PostMapping("/{id}/register")
    public Registration register(/* @PathVariable */ Long id,
                                  /* @RequestParam */ Long userId) {
        return opportunityService.registerStudent(userId, id);
    }

    // DELETE /api/opportunities/{id}/register?userId=
    // @DeleteMapping("/{id}/register")
    public void cancelRegistration(/* @PathVariable */ Long id,
                                    /* @RequestParam */ Long userId) {
        opportunityService.cancelRegistration(userId, id);
    }

    // GET /api/opportunities/{id}/roster  (organizer only)
    // @GetMapping("/{id}/roster")
    public List<Registration> getRoster(/* @PathVariable */ Long id) {
        return opportunityService.getRosterForOpportunity(id);
    }
}
