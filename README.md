# Scholars We Serve — Milestone 2: Backend Foundation

## Project Overview

Scholars We Serve is a web platform that connects high school students (ages 14–18) with age-appropriate volunteer opportunities. Students set a graduation date and required hours goal; the app calculates how many hours per week they need and tracks their progress in real time.

**Tech Stack:** Spring Boot (Java) backend · React (JavaScript) frontend · Volunteer.gov external API

---

## Backend Structure

### Package Organization

```
src/main/java/com/scholarsweserve/
├── model/
│   ├── User.java                  — Student & Organizer accounts
│   ├── ServiceOpportunity.java    — Volunteer listings (API-sourced or local)
│   ├── HourLog.java               — Individual completed-hour records
│   └── Registration.java          — Join table: student ↔ opportunity
│
├── repository/
│   ├── UserRepository.java
│   ├── ServiceOpportunityRepository.java
│   ├── HourLogRepository.java
│   └── RegistrationRepository.java
│
├── service/
│   ├── UserService.java           — Goal Dashboard + Weekly Target Calculator
│   ├── HourLogService.java        — Log submission + organizer approval workflow
│   └── ServiceOpportunityService.java — Discovery, filtering, registration
│
└── controller/
    ├── UserController.java        — /api/users/** (stub)
    ├── OpportunityController.java — /api/opportunities/** (stub)
    └── HourLogController.java     — /api/hours/** (stub)
```

### Key Design Decisions

**Why 4 models instead of 3?**
The milestone requires a minimum of 3. We added `Registration` as a fourth because User ↔ ServiceOpportunity is a genuine many-to-many relationship that needs its own table to track *when* a student signed up and whether they attended. Collapsing it into either parent model would make roster queries awkward and would duplicate data.

**Why is `spotsAvailable` decremented in the service layer?**
Decrementing happens inside `ServiceOpportunityService.registerStudent()` so the guard logic (duplicate check + availability check + decrement) stays atomic in one place. When we add a real database, this will become a transaction boundary.

**`isYouthFriendly` flag is set by the backend, not trusted from the API**
The Volunteer.gov API does not have a reliable "age 14–18 OK" field. Instead, `ServiceOpportunityService.isYouthEligible()` parses the title and description for adult-only language. This keeps the filtering logic centralized and testable.

**Weekly Target Calculator**
`UserService.calculateWeeklyTarget()` is the core value proposition of the app. Formula:

```
weeklyTarget = (totalHoursRequired - approvedHours) / (daysUntilGraduation / 7)
```

Edge cases handled: goal already met → returns 0.0, graduation passed → returns 999.0 (UI shows urgent warning).

---

## Product Backlog

| # | User Story | Priority |
|---|-----------|----------|
| 1 | As a student, I want to create an account so I can save my progress and goals. | HIGH |
| 2 | As a student, I want to enter my graduation date and required hours so the app can calculate my weekly target. | HIGH |
| 3 | As a student, I want to see a progress bar showing my current vs. goal hours so I stay motivated. | HIGH |
| 4 | As a student, I want to browse a filtered list of youth-friendly volunteer opportunities near me. | HIGH |
| 5 | As a student, I want to sign up for an opportunity so my spot is reserved. | HIGH |
| 6 | As a student, I want to log my completed volunteer hours so they count toward my goal. | HIGH |
| 7 | As an organizer, I want to log in with a separate role so I can manage my listings. | HIGH |
| 8 | As an organizer, I want to see a roster of students who signed up for my opportunity. | MEDIUM |
| 9 | As an organizer, I want to approve or reject submitted hour logs to verify participation. | MEDIUM |
| 10 | As a student, I want to search opportunities by keyword or category so I find the right fit. | MEDIUM |
| 11 | As a student, I want to cancel a registration if my plans change. | LOW |
| 12 | As an organizer, I want to create and edit my own local opportunities so students can find them. | LOW |

---

## Sprint 1 Plan (1 Week)

**Sprint Goal:** Deliver a working backend foundation with all models, repositories, and services implemented and tested manually.

| Backlog Item | Task | Assignee |
|-------------|------|----------|
| #1 — Student account creation | Implement `User` model + `UserRepository` + `UserService.createUser()` | **[Team Member A]** |
| #2 — Goal entry | Implement `UserService.updateGoal()` and `calculateWeeklyTarget()` calculator | **[Team Member A]** |
| #3 — Progress bar data | Implement `UserService.getGoalSummary()`, `HourLogRepository.sumApprovedHours()` | **[Team Member B]** |
| #6 — Hour logging | Implement `HourLog` model + `HourLogRepository` + `HourLogService` | **[Team Member B]** |
| #4 + #5 — Opportunity feed & sign-up | Implement `ServiceOpportunity`, `Registration`, both repositories, `ServiceOpportunityService` | **[Team Member C]** |

**Each team member owns at least one complete vertical slice** (model → repository → service) so work can proceed in parallel without merge conflicts.

---

## API Endpoint Reference (Controller Stubs)

### Users
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/users/register` | Create a new student or organizer account |
| GET | `/api/users/{id}` | Get user profile |
| GET | `/api/users/{id}/goal-summary` | Get dashboard data (progress bar + weekly target) |
| PUT | `/api/users/{id}/goal` | Update graduation date and required hours |

### Opportunities
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/opportunities` | List youth-friendly opportunities (optional `?keyword=`) |
| GET | `/api/opportunities/{id}` | Get single opportunity detail |
| POST | `/api/opportunities/{id}/register` | Sign up for an opportunity |
| DELETE | `/api/opportunities/{id}/register` | Cancel a registration |
| GET | `/api/opportunities/{id}/roster` | Organizer: view volunteer roster |

### Hours
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/hours` | Submit a completed hour log |
| GET | `/api/hours?userId={id}` | Get all logs for a student |
| GET | `/api/hours/pending` | Organizer: get all pending logs |
| PUT | `/api/hours/{id}/approve` | Approve a log |
| PUT | `/api/hours/{id}/reject` | Reject a log |

---
## Video Walkthrough: 
https://youtu.be/hSoC4OYwJkc

---

## AI Usage Log

| Tool | Prompt (Summary) | Purpose | How Output Was Used |
|------|-----------------|---------|---------------------|
| Claude (Sonnet) | "What service-layer methods would a volunteer hour tracking app need?" | Generate initial method list for UserService | Suggested `getApprovedHours`, `calculateWeeklyTarget`, `getGoalSummary`. Team added the `GoalSummary` inner DTO and the edge-case handling (goal met → 0.0, deadline passed → 999.0) which the AI did not include. |
| Claude (Sonnet) | "How should a Spring Boot service handle decrementing a spots counter safely?" | Understand the right layer for spot-decrement logic | Confirmed service layer is correct (not repository). Output didn't mention race conditions or transactions — team added a comment noting this will become a `@Transactional` boundary when the DB is connected. |
| ChatGPT (GPT-4o) | "What validation checks should I add when a student registers for an event?" | Identify edge cases for registration | Suggested duplicate-registration check and capacity check. Team also added a user-existence guard that GPT did not include. |
| ChatGPT (GPT-4o) | "Give me a formula to calculate weekly volunteer hours needed before a deadline" | Verify the math for the Weekly Target Calculator | Formula matched our approach. Output didn't handle the "graduation date already passed" edge case — team added the 999.0 sentinel and the UI warning note. |

**Team Reflection:** AI tools were most useful for generating a starting checklist of methods and validating our formula logic. In every case, the AI output was missing at least one edge case or design consideration that the team caught during review. No AI output was used verbatim — all code was written by team members with AI used as a sounding board, not a code generator.

---

## Next Steps (Future Milestones)

- Wire up H2 in-memory database (then migrate to PostgreSQL)
- Implement the Volunteer.gov API client in a `VolunteerGovApiService`
- Add Spring Security for authentication and role-based access
- Connect the React frontend to these endpoints
