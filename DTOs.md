# Unified Frontend DTO Contract

This file documents request/response payloads used by frontend.

## Base Notes
- `Instant` -> ISO-8601 string.
- `List<T>` -> array of `T`.
- Enum values are string values from backend enums unless stated otherwise.

## Core DTOs (`src/main/java/com/example/Job_Post/dto`)

### UserDTO
- `id: Integer`
- `nickName: String`
- `email: String`
- `status: String`
- `aboutMe: String`
- `number: String`
- `firstName: String`
- `lastName: String`
- `contactNumber: String`
- `profileImage: String`
- `averageRating: Double`
- `createdAt: Instant`
- `updatedAt: Instant`
- `skills: List<SkillDTO>`
- `files: List<FileDTO>`
- `newNotificationCount: Integer`
- `newChatMessageCount: Integer`
- `latestUnreadActionNotification: LatestUnreadActionNotificationDTO`
- `linkedIn: String`
- `preferredRole: String` (`EMPLOYER` | `EMPLOYEE` | `All`)
- `isCompany: Boolean` (default: `false`)
- `isFirstTimeAccessing: Boolean`
- `isAccountComplete: Boolean`

### CompanyDTO (extends UserDTO)
- `companyName: String`
- `website: String`
- `description: String`

### PreferredRoleDTO
- `preferredRole: String` (`EMPLOYER` | `EMPLOYEE` | `All`)

### SkillDTO
- `id: Integer`
- `skillType: String`
- `name: String`
- `level: int`
- `experience: String`

### FileDTO
- `id: Integer`
- `name: String`
- `url: String`
- `type: String`
- `size: Double`
- `isActive: Boolean`
- `uploadedAt: Instant`

### PostDTO
- `id: Integer`
- `poster: UserDTO`
- `title: String`
- `description: String`
- `isCompany: Boolean`
- `companyName: String`
- `location: String`
- `employmentType: String`
- `category: String`
- `postType: PostType` (`JOB_REQUEST` | `SERVICE_OFFER`)
- `salary: Double`
- `salaryRange: String`
- `salaryRangeLower: Double`
- `salaryRangeUpper: Double`
- `salaryCurrency: String`
- `salaryFrequency: String`
- `isNegotiable: Boolean`
- `serviceDeliveryDays: Integer`
- `serviceRevisionCount: Integer`
- `serviceIncludes: String`
- `portfolioUrl: String`
- `requirements: String`
- `responsibilities: String`
- `applicationDeadline: Instant`
- `postedTime: Instant`
- `imageUrls: List<String>`
- `isSavedByCurrentUser: Boolean`
- `applicationCount: Integer`

### JobApplicationDTO
- `id: Integer`
- `creatorDTO: UserDTO`
- `postDTO: PostDTO`
- `files: List<FileDTO>`
- `status: JobApplicationStatus`
- `firstName: String`
- `lastName: String`
- `contactNumber: String`
- `location: String`
- `other: String`
- `email: String`
- `appliedAt: Instant`
- `finalSalary: Double`

### SafeJobApplicationDTO
- `id: Integer`
- `creatorDTO: UserDTO`
- `postDTO: PostDTO`
- `status: JobApplicationStatus`
- `firstName: String`
- `lastName: String`
- `location: String`
- `email: String`

### ReviewDTO
- `id: Integer`
- `jobApplication: JobApplicationDTO`
- `writer: UserDTO`
- `receiver: UserDTO`
- `review: String`
- `rating: Integer`
- `createdAt: Instant`

### SalaryOfferDTO
- `id: Integer`
- `negotiationId: Integer`
- `sender: UserDTO`
- `proposedSalary: Double`
- `message: String`
- `accepted: boolean`
- `isResponded: boolean`
- `createdAt: Instant`

### SalaryNegotiationDTO
- `id: Integer`
- `jobApplication: JobApplicationDTO`
- `status: String`
- `offers: List<SalaryOfferDTO>`
- `createdAt: Instant`
- `initiator: UserDTO`

### SavedPostDTO
- `id: Integer`
- `postDTO: PostDTO`
- `userDTO: UserDTO`
- `savedAt: Instant`

### NotificationDTO
- `id: Integer`
- `notifiedUserDTO: UserDTO`
- `notificationType: String`
- `subjectId: Integer`
- `subjectType: String`
- `content: String`
- `isRead: boolean`
- `createdAt: Instant`

### LatestUnreadActionNotificationDTO
- `id: Integer`
- `notificationType: String`
- `chatUserId: Integer`
- `read: boolean`
- `createdAt: Instant`
- `content: String`

### ChatMessageDTO
- `id: Integer`
- `tempId: String`
- `recipientId: Integer`
- `senderId: Integer`
- `content: String`
- `chatRoomId: String`
- `isRead: Boolean`
- `timestamp: Instant`
- `isSystemGenerated: Boolean`

### ChatUserDTO
- `id: Integer`
- `nickName: String`
- `imageUrl: String`
- `email: String`
- `status: Status`
- `firstName: String`
- `lastName: String`
- `contactNumber: String`
- `hasUnseenMessageToCurrentUser: boolean`
- `relationship: ChatRelationshipStatus`

### UserWebSocketDTO
- `id: Integer`
- `email: String`
- `nickName: String`
- `status: Status`

### PagedResponse<T>
- `content: List<T>`
- `currentPage: int`
- `totalPages: int`
- `totalItems: long`
- `pageSize: int`

### DashboardSummaryDTO
- `preferredRole: String` (`EMPLOYER` | `EMPLOYEE` | `All`)
- `employee: EmployeeDashboardSummaryDTO | null`
- `employer: EmployerDashboardSummaryDTO | null`
- `topActionableItems: List<DashboardActionItemDTO>`

### EmployeeDashboardSummaryDTO
- `activeApplicationsCount: Integer`
- `savedCount: Integer`
- `pendingResponsesCount: Integer`
- `topActionableItems: List<DashboardActionItemDTO>`

### EmployerDashboardSummaryDTO
- `activePostsCount: Integer`
- `newApplicantsCount: Integer`
- `unreadMessagesCount: Integer`
- `topActionableItems: List<DashboardActionItemDTO>`

### DashboardActionItemDTO
- `type: String`
- `applicationId: Integer`
- `postId: Integer`
- `title: String`
- `subtitle: String`
- `createdAt: Instant`

## Auth Payloads (`src/main/java/com/example/Job_Post/auth`)

### RegisterRequest
- `firstName: String`
- `lastName: String`
- `email: String`
- `password: String`
- `phoneNumber: String`

### AuthenticationRequest
- `email: String`
- `password: String`

### AuthenticationResponse
- `token: String`

### DeleteRequest
- `email: String`
- `password: String`

## Endpoint Contract Additions (Preferred Role)

### Get current preferred role
- `GET /api/v1/user/preferred-role`
- Response: `PreferredRoleDTO`

### Update preferred role
- `PUT /api/v1/user/preferred-role`
- Request body: `PreferredRoleDTO`
- Response: `PreferredRoleDTO`
- Allowed values for `preferredRole`: `EMPLOYER`, `EMPLOYEE`, `All`

## Dashboard Endpoint

### Get dashboard summary
- `GET /api/v1/dashboard/summary`
- Auth required
- Response: `DashboardSummaryDTO`
- Role behavior:
- `EMPLOYEE` -> `employee` section only
- `EMPLOYER` -> `employer` section only
- `All` -> both sections + merged `topActionableItems`

### Counter semantics
- Employee:
- `activeApplicationsCount`: own non-withdrawn applications in statuses `APPLIED`, `OFFERED`, `HIRED`
- `savedCount`: own saved posts count
- `pendingResponsesCount`: own non-withdrawn applications in status `APPLIED`
- Employer:
- `activePostsCount`: count of posts created by current user
- `newApplicantsCount`: non-withdrawn applications in status `APPLIED` to current user's posts
- `unreadMessagesCount`: unread chat messages where current user is recipient

## Post Creation Notes
- `POST /api/v1/posts/create` supports `application/json` and `multipart/form-data`.
- `POST /api/v1/service-posts/create` supports `application/json` and `multipart/form-data`.
- Multipart keys:
- `post`: JSON payload matching `PostDTO`
- `images`: repeated file parts (up to 5)
