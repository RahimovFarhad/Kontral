# Post Image Upload (Frontend Integration)

This backend now supports creating a post with up to 5 images.

## Endpoint

`POST /api/v1/posts/create`

The endpoint supports two request formats:

1. `application/json`
Used for creating a post without images. This is the existing flow.

2. `multipart/form-data`
Used for creating a post with optional images.

## Multipart Request Contract

When sending `multipart/form-data`, send these parts:

- `post`
  The post payload as JSON matching `PostDTO`.
- `images`
  One or more image files. Use the same field name for each file.

Rules:

- Maximum 5 non-empty image files.
- Files above that limit return `400 Bad Request`.
- Image upload happens during post creation.
- Uploaded image URLs are stored on the backend and returned in the response.

## Example Request Shape

Frontend should send:

- `post`: JSON blob/string
- `images`: repeated file parts

Example `post` JSON:

```json
{
  "title": "Senior Frontend Engineer",
  "description": "React and TypeScript role",
  "isCompany": true,
  "companyName": "Example Inc",
  "location": "Stockholm",
  "employmentType": "Full-time",
  "category": "Software Development",
  "salaryRangeLower": 50000,
  "salaryRangeUpper": 70000,
  "salaryCurrency": "SEK",
  "salaryFrequency": "per month",
  "isNegotiable": true,
  "requirements": "3+ years React",
  "responsibilities": "Build frontend features",
  "applicationDeadline": "2026-03-31T00:00:00Z"
}
```

## Example Frontend (FormData)

```js
const formData = new FormData();

formData.append(
  "post",
  new Blob([JSON.stringify(postPayload)], { type: "application/json" })
);

for (const file of selectedFiles.slice(0, 5)) {
  formData.append("images", file);
}

await fetch("/api/v1/posts/create", {
  method: "POST",
  body: formData,
  credentials: "include"
});
```

## Response

The response is still `PostDTO`.

New field:

- `imageUrls: string[]`

This contains the uploaded image URLs in the same order they were processed by the backend.

## Notes

- Creating a post without images still works with plain JSON.
- This change only adds image support during post creation.
- Post edit image management is not implemented yet.
