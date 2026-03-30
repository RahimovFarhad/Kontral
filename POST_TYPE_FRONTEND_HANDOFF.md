# Post Type Frontend Handoff

This document summarizes the backend changes for introducing Fiverr-style service posts while preserving current job-post behavior.

## Goal

Support two listing types in the same `Post` model:

- `JOB_REQUEST` (existing behavior, default)
- `SERVICE_OFFER` (new "I can do X/Y" style posts)

Current behavior is intentionally preserved when frontend does nothing.

## Backend Changes Implemented

### 1) New enum

- `PostType` added:
  - `JOB_REQUEST`
  - `SERVICE_OFFER`

File:
- `src/main/java/com/example/Job_Post/enumerator/PostType.java`

### 2) `Post` entity extended

Added fields:

- `postType` (enum, default `JOB_REQUEST`)
- `serviceDeliveryDays` (`Integer`, optional)
- `serviceRevisionCount` (`Integer`, optional)
- `serviceIncludes` (`String`, optional)
- `portfolioUrl` (`String`, optional)

File:
- `src/main/java/com/example/Job_Post/entity/Post.java`

### 3) `PostDTO` extended

Added DTO fields:

- `postType`
- `serviceDeliveryDays`
- `serviceRevisionCount`
- `serviceIncludes`
- `portfolioUrl`

File:
- `src/main/java/com/example/Job_Post/dto/PostDTO.java`

### 4) Mapper defaults + roundtrip support

`PostMapper` now:

- maps new fields both ways
- defaults missing `postType` to `JOB_REQUEST`

File:
- `src/main/java/com/example/Job_Post/dto/PostMapper.java`

### 5) Service + filter behavior

`PostService` and `PostSpecification` now support `postType` filtering.

Important backward-compat behavior:

- if list endpoints omit `postType`, backend defaults to `job_request`
- existing DB rows with `postType = null` are treated as `JOB_REQUEST`

Files:
- `src/main/java/com/example/Job_Post/service/PostService.java`
- `src/main/java/com/example/Job_Post/specification/PostSpecification.java`

### 6) Controller query support

Updated endpoints with optional query param:

- `GET /api/v1/posts/all?postType=...`
- `GET /api/v1/posts/mine?postType=...`
- `GET /api/v1/posts/user/{userId}?postType=...`

Default:

- `postType=job_request` when omitted

File:
- `src/main/java/com/example/Job_Post/controller/PostController.java`

---

## Frontend Contract

## New/updated `PostDTO` fields

- `postType: "JOB_REQUEST" | "SERVICE_OFFER"` (optional on create/edit, defaults to `JOB_REQUEST`)
- `serviceDeliveryDays?: number`
- `serviceRevisionCount?: number`
- `serviceIncludes?: string`
- `portfolioUrl?: string`

All existing fields remain unchanged.

## Query parameter

- `postType` values for GET filters:
  - `job_request`
  - `service_offer`

Notes:

- filter query is case-insensitive
- omitted `postType` behaves as `job_request`

---

## Example Requests

## A) Existing job request (unchanged)

```http
POST /api/v1/posts/create
Content-Type: application/json
```

```json
{
  "title": "Need React mentor",
  "description": "Need help 3 times per week",
  "category": "Software Development",
  "employmentType": "Contract",
  "salary": 40,
  "salaryCurrency": "USD",
  "salaryFrequency": "per hour",
  "isNegotiable": true
}
```

No `postType` sent -> backend stores as `JOB_REQUEST`.

## B) New service offer post

```http
POST /api/v1/posts/create
Content-Type: application/json
```

```json
{
  "postType": "SERVICE_OFFER",
  "title": "I will build your landing page",
  "description": "Fast, responsive, SEO-ready",
  "category": "Web Development",
  "salary": 150,
  "salaryCurrency": "USD",
  "salaryFrequency": "total",
  "isNegotiable": true,
  "serviceDeliveryDays": 3,
  "serviceRevisionCount": 2,
  "serviceIncludes": "Design + implementation + deployment guidance",
  "portfolioUrl": "https://example.com/portfolio"
}
```

## C) New services tab fetch

```http
GET /api/v1/posts/all?postType=service_offer&sortBy=newest&page=0&size=10
```

## D) Existing jobs tab fetch (explicit)

```http
GET /api/v1/posts/all?postType=job_request&sortBy=newest&page=0&size=10
```

---

## Frontend Implementation Checklist

1. Add two feed tabs:
   - Jobs -> query `postType=job_request`
   - Services -> query `postType=service_offer`
2. Update create-post form:
   - add selector/toggle for post type
   - show service-specific inputs only for `SERVICE_OFFER`
3. Keep existing create/edit logic as-is for jobs:
   - safe because backend default remains `JOB_REQUEST`
4. Update "My posts" and "User profile posts" tabs similarly using `postType` query.
5. Keep current apply/order flow unchanged for now:
   - backend reuse strategy is active
   - label updates can be done in UI (`Apply` vs `Request Service`)

---

## What Was Intentionally Not Changed (Yet)

- Job application domain objects/endpoints were not renamed.
- Negotiation/review/notification flows were not split by post type yet.
- This is deliberate to keep rollout low-risk and maximize reuse.

---

## Compatibility Summary

- Existing frontend calls continue to work without modification.
- New feature is additive.
- Minimal backend migration risk due to defaults and null-safe filtering.
