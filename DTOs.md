# DTO Reference (Backend -> Frontend)

Source folder: `src/main/java/com/example/Job_Post/dto`

## ChatMessageDTO
- `id: Integer`
- `tempId: String`
- `recipientId: Integer`
- `senderId: Integer`
- `content: String`
- `chatRoomId: String`
- `isRead: Boolean`
- `timestamp: Instant`
- `isSystemGenerated: Boolean`

## ChatUserDTO
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

## CompanyDTO (extends UserDTO)
- `companyName: String`
- `website: String`
- `description: String`

## FileDTO
- `id: Integer`
- `name: String`
- `url: String`
- `type: String`
- `size: Double`
- `isActive: Boolean`
- `uploadedAt: Instant`

## JobApplicationDTO
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

## LatestUnreadActionNotificationDTO
- `id: Integer`
- `notificationType: String`
- `chatUserId: Integer`
- `read: boolean`
- `createdAt: Instant`
- `content: String`

## NotificationDTO
- `id: Integer`
- `notifiedUserDTO: UserDTO`
- `notificationType: String`
- `subjectId: Integer`
- `subjectType: String`
- `content: String`
- `isRead: boolean`
- `createdAt: Instant`

## PagedResponse<T>
- `content: List<T>`
- `currentPage: int`
- `totalPages: int`
- `totalItems: long`
- `pageSize: int`

## PostDTO
- `id: Integer`
- `poster: UserDTO`
- `title: String`
- `description: String`
- `isCompany: Boolean`
- `companyName: String`
- `location: String`
- `employmentType: String`
- `category: String`
- `salary: Double`
- `salaryRange: String`
- `salaryRangeLower: Double`
- `salaryRangeUpper: Double`
- `salaryCurrency: String`
- `salaryFrequency: String`
- `isNegotiable: Boolean`
- `requirements: String`
- `responsibilities: String`
- `applicationDeadline: Instant`
- `postedTime: Instant`
- `imageUrls: List<String>`
- `isSavedByCurrentUser: Boolean`
- `applicationCount: Integer`

### Post Creation Request Notes
- `POST /api/v1/posts/create` still accepts `application/json` for posts without images.
- `POST /api/v1/posts/create` also accepts `multipart/form-data` for posts with images.
- In multipart requests, send:
- `post`: JSON payload matching `PostDTO`
- `images`: repeated file parts (up to 5 images)
- On success, the response is `PostDTO`, including `imageUrls`.

## ReviewDTO
- `id: Integer`
- `jobApplication: JobApplicationDTO`
- `writer: UserDTO`
- `receiver: UserDTO`
- `review: String`
- `rating: Integer`
- `createdAt: Instant`

## SafeJobApplicationDTO
- `id: Integer`
- `creatorDTO: UserDTO`
- `postDTO: PostDTO`
- `status: JobApplicationStatus`
- `firstName: String`
- `lastName: String`
- `location: String`
- `email: String`

## SalaryNegotiationDTO
- `id: Integer`
- `jobApplication: JobApplicationDTO`
- `status: String`
- `offers: List<SalaryOfferDTO>`
- `createdAt: Instant`
- `initiator: UserDTO`

## SalaryOfferDTO
- `id: Integer`
- `negotiationId: Integer`
- `sender: UserDTO`
- `proposedSalary: Double`
- `message: String`
- `accepted: boolean`
- `isResponded: boolean`
- `createdAt: Instant`

## SavedPostDTO
- `id: Integer`
- `postDTO: PostDTO`
- `userDTO: UserDTO`
- `savedAt: Instant`

## SkillDTO
- `id: Integer`
- `skillType: String`
- `name: String`
- `level: int`
- `experience: String`

## UserDTO
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
- `isCompany: Boolean` (default: `false`)
- `isFirstTimeAccessing: Boolean`
- `isAccountComplete: Boolean`

## UserWebSocketDTO
- `id: Integer`
- `email: String`
- `nickName: String`
- `status: Status`

## Enum / External Type Notes
- `Instant` -> ISO date-time string in JSON
- `List<T>` -> array of `T`
- `Status`, `ChatRelationshipStatus`, `JobApplicationStatus` are backend enums
