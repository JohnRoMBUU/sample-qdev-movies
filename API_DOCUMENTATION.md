# Movie Search API Documentation 🏴‍☠️

Ahoy, matey! This document be yer treasure map to understanding the movie search functionality in our Spring Boot application. Arrr!

## Overview

The Movie Search API provides comprehensive search and filtering capabilities for the movie catalog, featuring:
- Case-insensitive partial text matching
- Exact ID-based lookups
- Genre-based filtering
- Combined search criteria support
- Pirate-themed user experience
- Robust error handling

## API Endpoints

### 1. Search Movies Endpoint

**Endpoint:** `GET /movies/search`

**Description:** Search and filter movies based on provided criteria. Returns HTML template with filtered results and pirate-themed messaging.

#### Request Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `name` | String | No | Movie name to search for (case-insensitive partial match) | `prison`, `hero`, `the` |
| `id` | Long | No | Exact movie ID to find (must be positive integer) | `1`, `5`, `12` |
| `genre` | String | No | Genre to filter by (case-insensitive partial match) | `drama`, `action`, `sci` |

#### Request Examples

```bash
# Search by movie name
curl "http://localhost:8080/movies/search?name=prison"

# Search by exact ID
curl "http://localhost:8080/movies/search?id=1"

# Search by genre
curl "http://localhost:8080/movies/search?genre=drama"

# Combined search (name AND genre)
curl "http://localhost:8080/movies/search?name=the&genre=action"

# Search with no criteria (returns all movies)
curl "http://localhost:8080/movies/search"
```

#### Response

**Content-Type:** `text/html`

**Template:** `movies.html`

**Model Attributes:**

| Attribute | Type | Description |
|-----------|------|-------------|
| `movies` | `List<Movie>` | Filtered list of movies matching search criteria |
| `genres` | `List<String>` | All available genres for dropdown population |
| `searchPerformed` | `Boolean` | Flag indicating a search was performed |
| `searchName` | `String` | Preserved search name parameter |
| `searchId` | `Long` | Preserved search ID parameter |
| `searchGenre` | `String` | Preserved search genre parameter |
| `successMessage` | `String` | Pirate-themed success message (when results found) |
| `noResultsMessage` | `String` | Pirate-themed no results message |
| `errorMessage` | `String` | Pirate-themed error message (for invalid input) |

#### Response Messages

**Success Messages:**
- Single result: `"Ahoy! Found 1 movie treasure for ye, matey!"`
- Multiple results: `"Shiver me timbers! Found X movie treasures in our chest!"`

**Error Messages:**
- No results: `"Arrr! No treasure found with those search terms, ye scurvy dog! Try different criteria to find yer movie bounty!"`
- Invalid ID: `"Shiver me timbers! That ID be not a valid treasure map number, matey!"`
- Server error: `"Batten down the hatches! Something went wrong with the search, ye landlubber! Try again later."`

### 2. Get All Movies Endpoint

**Endpoint:** `GET /movies`

**Description:** Returns HTML page with all movies and the search form interface.

**Enhanced Features:**
- Includes search form at the top of the page
- Populates genre dropdown with all available genres
- Maintains existing movie display functionality

## Service Layer Documentation

### MovieService.searchMovies()

**Method Signature:**
```java
public List<Movie> searchMovies(String name, Long id, String genre)
```

**Parameters:**
- `name`: Movie name search term (case-insensitive partial match)
- `id`: Exact movie ID to find
- `genre`: Genre search term (case-insensitive partial match)

**Return Value:** `List<Movie>` - Filtered list of movies matching all provided criteria

**Search Logic:**
1. If all parameters are null/empty, returns all movies
2. Applies AND logic for multiple criteria (all must match)
3. Name matching: case-insensitive contains check
4. ID matching: exact equality check
5. Genre matching: case-insensitive contains check

**Example Usage:**
```java
// Search by name only
List<Movie> results = movieService.searchMovies("prison", null, null);

// Search by multiple criteria
List<Movie> results = movieService.searchMovies("the", null, "drama");

// Get all movies
List<Movie> results = movieService.searchMovies(null, null, null);
```

### MovieService.getAllGenres()

**Method Signature:**
```java
public List<String> getAllGenres()
```

**Return Value:** `List<String>` - Sorted list of unique genres from all movies

**Usage:** Populates genre dropdown in search form

## Data Model

### Movie Entity

```java
public class Movie {
    private final long id;
    private final String movieName;
    private final String director;
    private final int year;
    private final String genre;
    private final String description;
    private final int duration;
    private final double imdbRating;
    
    // Getters and constructor...
}
```

### Available Movies

The application includes 12 movies with the following genres:
- Drama
- Crime/Drama
- Action/Crime
- Action/Sci-Fi
- Drama/Romance
- Adventure/Fantasy
- Adventure/Sci-Fi
- Drama/History
- Drama/Thriller

## Error Handling

### Input Validation

**Invalid ID Handling:**
- Negative or zero IDs are rejected
- Returns empty results with pirate-themed error message
- Logs warning for invalid ID attempts

**Parameter Sanitization:**
- Null parameters are handled gracefully
- Empty strings are treated as null
- Whitespace is trimmed from search terms

### Exception Handling

**Controller Level:**
- Try-catch blocks around search operations
- Graceful degradation on service failures
- Pirate-themed error messages for users
- Proper logging of exceptions

**Service Level:**
- Defensive programming against null inputs
- Safe iteration over movie collections
- Logging of search operations and results

## Performance Considerations

### Search Optimization

**Current Implementation:**
- In-memory search through loaded movie list
- O(n) time complexity for each search
- Suitable for small datasets (12 movies)

**Scalability Notes:**
- For larger datasets, consider:
  - Database-backed search with indexed queries
  - Caching of search results
  - Pagination for large result sets
  - Search result limiting

### Memory Usage

- Movies loaded once at startup
- Search creates new result lists (no mutation of original data)
- Minimal memory overhead for search operations

## Testing

### Unit Test Coverage

**MovieServiceTest:**
- Tests all search scenarios (name, ID, genre, combined)
- Edge cases (empty criteria, no matches, invalid input)
- Whitespace handling and case sensitivity
- Genre listing functionality

**MoviesControllerTest:**
- Controller endpoint testing with mock services
- Parameter handling and validation
- Response model attribute verification
- Error scenario testing
- Pirate message validation

### Test Examples

```java
@Test
public void testSearchMovies_PartialNameMatch_ReturnsCorrectMovies() {
    List<Movie> results = movieService.searchMovies("the", null, null);
    
    assertTrue(results.size() > 0);
    for (Movie movie : results) {
        assertTrue(movie.getMovieName().toLowerCase().contains("the"));
    }
}
```

## Security Considerations

### Input Sanitization

- All search parameters are validated
- No SQL injection risk (in-memory search)
- XSS protection through Thymeleaf templating
- Parameter length limits enforced by browser

### Error Information Disclosure

- Generic error messages for users
- Detailed error information only in logs
- No sensitive system information exposed

## Future Enhancements

### Potential Improvements

1. **Advanced Search Features:**
   - Year range filtering
   - Rating-based filtering
   - Director search
   - Duration-based filtering

2. **Search Experience:**
   - Auto-complete suggestions
   - Search history
   - Saved searches
   - Search result sorting

3. **Performance Optimizations:**
   - Full-text search indexing
   - Search result caching
   - Pagination support
   - Lazy loading

4. **API Enhancements:**
   - JSON API endpoints
   - RESTful search parameters
   - Search result metadata
   - Search analytics

## Troubleshooting

### Common Issues

**No Search Results:**
- Verify search terms match movie data
- Check for typos in search parameters
- Ensure ID parameters are positive integers

**Form Not Submitting:**
- Check JavaScript console for errors
- Verify form action URL is correct
- Ensure server is running on correct port

**Pirate Messages Not Displaying:**
- Verify Thymeleaf template syntax
- Check model attributes in controller
- Ensure CSS classes are properly applied

### Debug Information

**Logging:**
- Search operations logged at INFO level
- Errors logged at ERROR level with stack traces
- Search criteria and result counts included in logs

**Development Tools:**
- Use browser developer tools to inspect form submission
- Check network tab for request/response details
- Verify model attributes in template debugging

---

*Arrr! May this documentation guide ye safely through the treacherous waters of movie search implementation, ye savvy developer! 🏴‍☠️*