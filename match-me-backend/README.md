# Match Me

A full-stack web application that connects users based on their profiles, interests, and preferences. Match Me uses a sophisticated recommendation algorithm to suggest compatible connections, enabling users to form meaningful relationships whether for friendship, collaboration, dating, or professional networking.

## Features

### Core Functionality

- **User Registration & Authentication**: Secure sign-up with bcrypt password hashing and JWT-based session management
- **Comprehensive User Profiles**: Multi-step profile creation including display name, bio, interests, hobbies, and preferences
- **Smart Recommendations**: Advanced scoring algorithm considering 7+ biographical data points and location proximity
- **Location-Based Matching**: GPS coordinate tracking with configurable radius preferences
- **Connection Management**: Request, accept, reject, and disconnect from other users
- **Real-Time Chat**: WebSocket-powered instant messaging with typing indicators and unread notifications
- **Profile Picture Upload**: Image upload with validation and storage
- **Online/Offline Status**: Real-time presence tracking

### Technical Highlights

- **RESTful API**: Clean, unopinionated endpoints following REST principles
- **Real-Time Communication**: WebSocket (STOMP) for chat and presence updates
- **Secure by Design**: JWT authentication, bcrypt password hashing, permission-based profile viewing
- **Pagination Support**: Efficient data loading for chat history and recommendations
- **Responsive Design**: Mobile and desktop-friendly interface

## Technology Stack

### Backend

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.6
- **Database**: PostgreSQL 15
- **Security**: Spring Security with JWT
- **Real-Time**: WebSocket (STOMP protocol)
- **Build Tool**: Maven

### Frontend

- **Framework**: React 19.2.0
- **Language**: TypeScript 4.9.5
- **Package Manager**: npm
- **Styling**: Inline styles with modern CSS

### Infrastructure

- **Containerization**: Docker (PostgreSQL)
- **File Storage**: Local filesystem

## Prerequisites

- Java 21 or higher
- Node.js 16+ and npm
- Docker Desktop (for PostgreSQL)
- Git

## Setup and Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd match-me
```

### 2. Database Setup

Start PostgreSQL using Docker:

```bash
docker run --name matchme-postgres \
  -e POSTGRES_PASSWORD=mypassword123 \
  -e POSTGRES_DB=matchme_db \
  -p 5432:5432 \
  -d postgres:15
```

Verify the database is running:

```bash
docker ps | grep matchme-postgres
```

### 3. Backend Setup

Navigate to backend directory:

```bash
cd match-me-backend
```

The application uses the following database credentials (configured in `application.properties`):

- **URL**: `jdbc:postgresql://localhost:5432/matchme_db`
- **Username**: `postgres`
- **Password**: `mypassword123`

Build and run the backend:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

The backend will start on **http://localhost:8080**

#### Seed Test Data (Optional)

To populate the database with 120 test users:

```bash
.\mvnw.cmd spring-boot:run --spring.profiles.active=seed
```

All seeded users have the password: `password123`

### 4. Frontend Setup

Open a new terminal and navigate to frontend directory:

```bash
cd match-me-frontend
npm install
npm start
```

The frontend will start on **http://localhost:3000** and open in your browser automatically.

## Usage Guide

### Getting Started

1. **Register**: Create an account with email and password (username auto-generated and unique)
2. **Complete Profile**: Fill in your display name and about me section
3. **Add Bio Information**: Provide interests, hobbies, food preferences, music taste, personality type, and what you're looking for
4. **Set Location**: Specify your location and maximum search radius
5. **View Recommendations**: Browse potential matches (max 10 at a time, ranked by compatibility score)

### Using the Application

#### Recommendations

- View up to 10 recommendations at a time
- **Connect**: Send a connection request
- **Pass**: Dismiss the recommendation (won't be shown again)
- Recommendations are ranked by compatibility score

#### Connections

- View incoming connection requests
- Accept or reject requests
- View all connected users
- Disconnect from users at any time

#### Chat

- Start conversations only with connected users (connection required)
- Real-time message delivery via WebSocket
- See typing indicators when others are typing
- Unread message notifications
- Message history with pagination
- Disconnecting from a user automatically removes the chat room and clears unread messages

#### Profile Management

- Update profile information anytime
- Change profile picture
- Modify bio and preferences
- Adjust location and search radius

## 🔌 API Endpoints

### Authentication

- `POST /auth/register` - Register new user (email + password; username auto-generated and unique)
- `POST /auth/login` - Login and receive JWT token
- `POST /auth/logout` - Logout (client removes JWT token)
- `GET /auth/me` - Get current user info

### Users

- `GET /users/{id}` - Get user's basic info (id, displayName, avatarUrl)
- `GET /users/{id}/profile` - Get user's profile (id, userId, displayName, aboutMe, avatarUrl)
- `GET /users/{id}/bio` - Get user's biographical data
- `GET /users/me` - Get current user
- `GET /users/me/profile` - Get current user's profile
- `GET /users/me/bio` - Get current user's bio

### Profile Management

- `POST /profile` - Create profile
- `PUT /profile/{userId}` - Update profile
- `GET /profile/{userId}` - Get profile

### Bio Management

- `POST /bio` - Create bio
- `PUT /bio/{userId}` - Update bio
- `GET /bio/{userId}` - Get bio

### Location

- `POST /locations` - Set location
- `PUT /locations` - Update location
- `GET /locations/{userId}` - Get user's location

### Recommendations

- `GET /recommendations` - Get up to 10 recommendations (returns only IDs)
- `POST /recommendations/{userId}/dismiss` - Dismiss a recommendation

### Connections

- `GET /connections` - Get list of connected users (returns only IDs)
- `GET /connections/requests` - Get pending connection requests
- `POST /connections/request/{userId}` - Request connection
- `PUT /connections/{id}/accept` - Accept connection request
- `PUT /connections/{id}/reject` - Reject connection request
- `DELETE /connections/{otherUserId}` - Disconnect from user (auto-deletes related chat rooms and clears unread messages)

### Chat

- `GET /chat/chats` - Get all chat rooms
- `GET /chat/chats/{chatId}/messages` - Get paginated messages
- `GET /chat/chats/unread-count` - Get unread message count
- `PUT /chat/chats/{chatId}/mark-read` - Mark messages as read
- `WS /chat.send` - Send message via WebSocket

### Profile Picture Management

- `POST /me/profile/picture` - Upload profile picture
- `DELETE /me/profile/picture` - Delete profile picture
- `GET /api/images/{filename}` - Retrieve image

## Testing

### Test Users

After running the seed script, you can login with any seeded user:

- Email pattern: `{name}{index}@matchme.example.com` (e.g., `silva0@matchme.example.com`, `jüri1@matchme.example.com`)
- Password: `password123`
- Total: 120 test users with complete profiles, bios, and locations

## Troubleshooting

### Port 8080 Already in Use

**Problem**: Backend fails to start with "Port 8080 was already in use"

**Solution**: Kill the existing process and restart

```bash
# Find and kill process using port 8080
lsof -i :8080 | grep -v COMMAND | awk '{print $2}' | xargs kill -9

# Or simply restart the backend
./mvnw spring-boot:run
```

**Prevention**: Always stop the backend cleanly before restarting:
- In the terminal running the backend, press `Ctrl+C`
- Wait 2-3 seconds for the port to be released
- Then start the backend again

### Drop and Reload Database

```bash
# Stop and remove container
docker stop matchme-postgres
docker rm matchme-postgres

# Recreate database
docker run --name matchme-postgres \
  -e POSTGRES_PASSWORD=mypassword123 \
  -e POSTGRES_DB=matchme_db \
  -p 5432:5432 \
  -d postgres:15

# Restart backend with seed profile
.\mvnw.cmd spring-boot:run --spring.profiles.active=seed
```

## Security Features

- **Password Security**: Bcrypt hashing with automatic salt generation
- **JWT Authentication**: Stateless session management with logout endpoint
- **Email Privacy**: Email addresses never exposed via API (excluded from all responses)
- **Permission-Based Access**: Profiles only viewable if:
  - User is recommended
  - Connection request exists
  - Users are connected
- **Chat Security**: Messages only accessible between active connected users; disconnecting blocks access and clears history
- **File Upload Validation**:
  - Only image files allowed
  - 5MB size limit
  - Secure filename generation
- **CORS Protection**: Configured for localhost:3000

## Recommendation Algorithm

The matching algorithm considers multiple factors with weighted scoring:

1. **Location Proximity (0-5 points)**: Closer matches score higher
2. **Looking For Match (3 points)**: Mutual interest in connection type
3. **Hobbies Overlap (2 points)**: Shared hobbies
4. **Interests Match (2 points)**: Common interests
5. **Food Preferences (1 point)**: Similar food tastes
6. **Music Taste (1 point)**: Compatible music preferences
7. **Personality Compatibility (2.5 points)**: Personality type matching

**Minimum Score**: 3.0 points required to be recommended
**Maximum Results**: 10 recommendations per request

## Project Structure

```
match-me/
├── match-me-backend/
│   ├── src/main/java/com/matchme/match_me/
│   │   ├── auth/           # Authentication & JWT
│   │   ├── bio/            # User biographical data
│   │   ├── chat/           # Real-time chat
│   │   ├── config/         # Security, CORS, WebSocket
│   │   ├── connections/    # Connection management
│   │   ├── location/       # Location & proximity
│   │   ├── profile/        # User profiles & images
│   │   ├── recommendations/# Matching algorithm
│   │   ├── seed/           # Test data generation
│   │   └── users/          # User management
│   └── src/main/resources/
│       └── application.properties
└── match-me-frontend/
    ├── src/
    │   ├── pages/          # React pages
    │   ├── services/       # API communication
    │   ├── App.tsx         # Main app component
    │   └── index.tsx       # Entry point
    └── package.json
```

## Troubleshooting

### Backend won't start

- Ensure PostgreSQL is running: `docker ps`
- Check database credentials in `application.properties`
- Verify Java 21 is installed: `java -version`

### Frontend won't connect

- Confirm backend is running on port 8080
- Check CORS configuration in `CorsConfig.java`
- Clear browser cache and localStorage
