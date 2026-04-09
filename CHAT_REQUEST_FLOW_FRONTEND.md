# Chat Request/Accept/Block Contract (Frontend)

This document describes only the new messaging flow behavior and API contract updates.

## DTO Changes

### `ChatUserDTO`
New field:
- `chatState: ChatState | null`

Possible values:
- `REQUEST_PENDING`
- `ACTIVE`
- `BLOCKED`

Notes:
- Existing `relationship` is unchanged (job/application relationship).
- `chatState` may be `null` when there is no visible chat room with that user.

## Response/Endpoint Changes

### 1) Chat users endpoint now supports filtering

`GET /api/v1/user/chat/users?include=<value>`

`include` is optional and case-insensitive:
- `all` (default)
- `active`
- `pending`
- `blocked`

Behavior:
- If omitted, backend behaves as `include=all`.
- If invalid value is passed, backend falls back to `all`.

Response shape remains `ChatUserDTO[]`, now with `chatState`.
Returned list is the current user's visible chat counterparts (not all platform users).

---

### 2) New explicit accept endpoint

`POST /api/v1/chat/{otherUserId}/accept`

Success:
- `200 OK`
- body: `ACTIVE` (enum string)

Behavior:
- Request is accepted only through this endpoint.
- No automatic accept on first reply.

---

### 3) New block/unblock endpoints

`POST /api/v1/chat/{otherUserId}/block`
- Success: `200 OK`
- body: `"Chat blocked"`

`POST /api/v1/chat/{otherUserId}/unblock`
- Success: `200 OK`
- body: `"Chat unblocked"`

## Messaging Behavior Change

For STOMP send (`/app/chat` with `ChatMessageDTO`):
- If `chatState=REQUEST_PENDING`, only the initiator can send messages.
- Recipient cannot reply until `POST /accept` is called.
- If `chatState=BLOCKED`, sending is rejected.

## Error Messages to Handle

### Core reason strings (from service/controller logic)
- `Messages are blocked in this chat`
- `Chat request must be accepted before replying`
- `Cannot accept a blocked chat`
- `Request initiator cannot accept own request`
- `Only blocker can unblock this chat`

### REST error wrapper format (controller responses)
- `Failed to block chat: <reason>`
- `Failed to unblock chat: <reason>`
- `Failed to accept chat request: <reason>`
- `Failed to retrieve chat users: <reason>`

Frontend should parse these as displayable error strings.
