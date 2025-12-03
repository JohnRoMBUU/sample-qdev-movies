# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Search & Filtering**: 🏴‍☠️ Search for movie treasures by name, ID, or genre with pirate-themed interface
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **Pirate Language**: Arrr! Enjoy the nautical-themed messages and error handling throughout the application

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Search**: http://localhost:8080/movies/search (with query parameters)
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic for movie operations and search
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie catalog data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Movie list and search interface
│           └── movie-details.html            # Movie detail page
└── test/                                     # Unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java         # Tests for movie service and search
            ├── MoviesControllerTest.java     # Tests for controller endpoints
            └── MovieTest.java                # Tests for movie model
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information. Includes a search form for filtering movies.

### Search Movies 🏴‍☠️
```
GET /movies/search
```
Search and filter movies by various criteria. Returns the same movies template with filtered results and pirate-themed messages.

**Query Parameters:**
- `name` (optional): Movie name to search for (case-insensitive partial match)
- `id` (optional): Exact movie ID to find (must be positive integer)
- `genre` (optional): Genre to filter by (case-insensitive partial match)

**Examples:**
```
# Search by movie name
http://localhost:8080/movies/search?name=prison

# Search by exact ID
http://localhost:8080/movies/search?id=1

# Search by genre
http://localhost:8080/movies/search?genre=drama

# Combined search (name AND genre)
http://localhost:8080/movies/search?name=the&genre=action

# Search with no criteria (returns all movies)
http://localhost:8080/movies/search
```

**Response Messages:**
- **Success**: "Ahoy! Found X movie treasure(s) for ye, matey!"
- **No Results**: "Arrr! No treasure found with those search terms, ye scurvy dog!"
- **Invalid ID**: "Shiver me timbers! That ID be not a valid treasure map number, matey!"
- **Error**: "Batten down the hatches! Something went wrong with the search, ye landlubber!"

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## Search Functionality 🏴‍☠️

The movie search feature provides a comprehensive way to find movies in the catalog with a pirate-themed interface:

### Search Interface
- **Interactive Form**: Located at the top of the movies page with pirate-themed styling
- **Multiple Criteria**: Search by name, ID, genre, or any combination
- **Real-time Feedback**: Form preserves search values and displays helpful messages
- **Responsive Design**: Works seamlessly on desktop and mobile devices

### Search Capabilities
- **Name Search**: Case-insensitive partial matching (e.g., "prison" finds "The Prison Escape")
- **ID Search**: Exact match for specific movie lookup
- **Genre Search**: Case-insensitive partial matching (e.g., "sci" finds "Action/Sci-Fi")
- **Combined Search**: All criteria must match (AND logic)
- **Empty Search**: Returns all movies when no criteria provided

### Error Handling
- **Invalid IDs**: Gracefully handles negative or zero IDs with pirate messages
- **No Results**: Friendly pirate-themed message when no movies match
- **Server Errors**: Robust error handling with nautical-themed feedback
- **Input Validation**: Client-side and server-side validation for all parameters

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new features like advanced search filters or sorting
- Improve the responsive design
- Extend the search functionality with additional criteria
- Add more pirate language and nautical themes

### Recent Enhancements 🏴‍☠️
- **Movie Search & Filtering**: Complete search functionality with name, ID, and genre filtering
- **Pirate Language Integration**: Nautical-themed messages and error handling throughout
- **Enhanced UI**: Pirate-themed search form with responsive design
- **Comprehensive Testing**: Full unit test coverage for search functionality
- **Robust Error Handling**: Graceful handling of edge cases with themed messages

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.
