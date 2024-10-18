# Movies Application

## Overview

A complete movies application that integrates with IMDb APIs, featuring a user dashboard and an admin dashboard for enhanced management and user interaction.

## Features

- **User Registration and Login**: Users can register and log in to access their dashboards.
- **Movie Management**: Admins can add, remove, and search for movies in the database.
- **Movie Rating**: Users can rate movies they have watched from the database (which the admin added).

## Endpoints

### Authentication Controller

- **Login**
  - **POST** `/login`
    - User login endpoint.
    - **Request Body**: `LoginRequestDto`
    - **Response**: `LoginResponseDto` containing authentication details.

- **Register**
  - **POST** `/register`
    - User registration endpoint.
    - **Request Body**: `RegisterRequestDto`
    - **Response**: User details of the newly registered user.


### Admin Controller

- **Dashboard**
  - **GET** `/admin/dashboard`
    - Search for movies in OMDB API websit by title. (https://www.omdbapi.com/)
    - **Request Params**: `title` (String)
    - **Response**: List of movies matching the search criteria.

- **Add Movie to Database**
  - **POST** `/admin/dashboard/add`
    - Add a movie to the database using its IMDb ID.
    - **Request Params**: `imdbID` (String)
    - **Response**: The added movie details.

- **Batch Remove Movies**
  - **DELETE** `/admin/dashboard/batch-remove`
    - Remove multiple movies from the database.
    - **Request Body**: List of IMDb IDs (String)
    - **Response**: 200 OK on successful deletion.

- **Batch Add Movies**
  - **POST** `/admin/dashboard/batch-add`
    - Add multiple movies to the database.
    - **Request Body**: List of IMDb IDs (String)
    - **Response**: List of added movies.

- **Remove Movie**
  - **DELETE** `/admin/dashboard/remove`
    - Remove a single movie using its IMDb ID.
    - **Request Params**: `imdbID` (String)

### User Controller

- **User Dashboard**
  - **GET** `/user/dashboard`
    - Retrieve a paginated list of movies for the user.
    - **Request Params**: `page` (Integer), `size` (Integer)
    - **Response**: `PaginatedMoviesResponse` containing a list of movies.

- **Get Movie Details**
  - **GET** `/user/dashboard/movie/{imdbID}`
    - Retrieve details of a specific movie by its IMDb ID.
    - **Response**: Movie details.

- **Add Rating**
  - **POST** `/user/dashboard/rating/{imdbID}`
    - Submit a rating for a movie.
    - **Request Params**: `rating` (Double), `userId` (Long)

- **Search Movies**
  - **GET** `/user/dashboard/search`
    - Search for movies by title with pagination.
    - **Request Params**: `title` (String), `page` (Integer), `size` (Integer)
    - **Response**: `PaginatedMoviesResponse` containing matching movies.

## Technologies Used

- Spring Boot
- Angular

## Getting Started

### Prerequisites

**Backend**:
- Java 8+
- Maven

**Frontend**:
- Node.js
- Angular 16+ CLI

## Frontend (Angular)

To run the frontend of the Movie Management System, follow these steps:

1. **Clone the Angular Repository**:
   ```bash
   git clone https://github.com/mostafaFat7i/Angular-Movie-App
   cd Angular-Movie-App
2. **Install Dependencies**: Make sure you have Node.js and npm installed. Then, run the following command to install the required packages:
   ```bash
   npm install
3. **Run the Angular Application**: Start the Angular development server:
   ```bash
   ng serve
The frontend application will be available at http://localhost:4200.

### Backend Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/movie-management-system.git
   cd movie-management-system
2. Build the project:
   ```bash
   mvn clean install
3. Run the application:
   ```bash
   mvn spring-boot:run

The backend application will be available at http://localhost:8090.

### Configuration
- Make sure to set up the database configurations in the application.properties file according to your environment. You will find two versions of the application.properties file: application-dev.properties for the development profile and application-prod.properties for the production profile. You can swap between them by modifying the application.properties file.





