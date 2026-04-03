# Chat Pagination Contract (Frontend)

This is a minimal backend update to avoid loading full chat history on each open.

## Endpoint

`GET /api/v1/chat/messages/{senderId}/{recipientId}`

## Query params

- `limit` (optional, integer): page size.
- `before` (optional, ISO-8601 datetime): fetch messages older than this timestamp (exclusive).

If `limit` is omitted, backend uses `50`.

Valid `limit` range is `1..200`.

## Response order

Messages are returned in chronological order (oldest -> newest) for each page.

## Pagination behavior

- First page (latest messages):
  - `GET /api/v1/chat/messages/12/45?limit=50`
- Next older page:
  - take the first message in current list (oldest loaded),
  - use its `timestamp` as `before`,
  - `GET /api/v1/chat/messages/12/45?limit=50&before=2026-04-03T10:20:30.123Z`

Stop requesting older pages when response is empty.

## Frontend implementation notes

- On chat open: load first page only.
- On scroll-to-top: load next page with `before`.
- Prepend older page to existing state.
- Deduplicate by `id` when merging.
- Keep existing WebSocket real-time append behavior unchanged.
