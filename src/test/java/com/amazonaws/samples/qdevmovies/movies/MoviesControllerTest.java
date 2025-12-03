package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MoviesController including search functionality.
 * Arrr! These tests be making sure our controller handles all requests like a proper ship captain!
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if (id == 2L) {
                    return Optional.of(new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                // Simple mock search logic
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matches = movie.getMovieName().toLowerCase().contains(name.toLowerCase());
                    }
                    
                    if (matches && id != null) {
                        matches = movie.getId().equals(id);
                    }
                    
                    if (matches && genre != null && !genre.trim().isEmpty()) {
                        matches = movie.getGenre().toLowerCase().contains(genre.toLowerCase());
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies template with all movies and genres")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("genres"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size());
        
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) model.getAttribute("genres");
        assertEquals(2, genres.size());
    }

    @Test
    @DisplayName("Should return movie details for valid ID")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        
        assertNotNull(result);
        assertEquals("movie-details", result);
        assertTrue(model.containsAttribute("movie"));
        assertTrue(model.containsAttribute("movieIcon"));
        assertTrue(model.containsAttribute("allReviews"));
    }

    @Test
    @DisplayName("Should return error page for invalid movie ID")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        
        assertNotNull(result);
        assertEquals("error", result);
        assertTrue(model.containsAttribute("title"));
        assertTrue(model.containsAttribute("message"));
        
        String message = (String) model.getAttribute("message");
        assertTrue(message.contains("Arrr!"));
        assertTrue(message.contains("Davy Jones' locker"));
    }

    @Test
    @DisplayName("Should search movies with no criteria and return all movies")
    public void testSearchMovies_NoCriteria_ReturnsAllMovies() {
        String result = moviesController.searchMovies(null, null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size());
        
        Boolean searchPerformed = (Boolean) model.getAttribute("searchPerformed");
        assertTrue(searchPerformed);
    }

    @Test
    @DisplayName("Should search movies by name and return matching results")
    public void testSearchMovies_ByName_ReturnsMatchingMovies() {
        String result = moviesController.searchMovies("Test", null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("searchName"));
        assertTrue(model.containsAttribute("successMessage"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        String searchName = (String) model.getAttribute("searchName");
        assertEquals("Test", searchName);
        
        String successMessage = (String) model.getAttribute("successMessage");
        assertTrue(successMessage.contains("Ahoy!"));
    }

    @Test
    @DisplayName("Should search movies by ID and return exact match")
    public void testSearchMovies_ById_ReturnsExactMatch() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchId"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals(2L, movies.get(0).getId());
        
        Long searchId = (Long) model.getAttribute("searchId");
        assertEquals(2L, searchId);
    }

    @Test
    @DisplayName("Should search movies by genre and return matching results")
    public void testSearchMovies_ByGenre_ReturnsMatchingMovies() {
        String result = moviesController.searchMovies(null, null, "Drama", model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchGenre"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Drama", movies.get(0).getGenre());
        
        String searchGenre = (String) model.getAttribute("searchGenre");
        assertEquals("Drama", searchGenre);
    }

    @Test
    @DisplayName("Should handle invalid ID gracefully")
    public void testSearchMovies_InvalidId_ReturnsErrorMessage() {
        String result = moviesController.searchMovies(null, -1L, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("errorMessage"));
        assertTrue(model.containsAttribute("movies"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
        
        String errorMessage = (String) model.getAttribute("errorMessage");
        assertTrue(errorMessage.contains("Shiver me timbers!"));
        assertTrue(errorMessage.contains("not a valid treasure map number"));
    }

    @Test
    @DisplayName("Should return no results message when no movies match criteria")
    public void testSearchMovies_NoMatches_ReturnsNoResultsMessage() {
        String result = moviesController.searchMovies("NonexistentMovie", null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("noResultsMessage"));
        assertTrue(model.containsAttribute("movies"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
        
        String noResultsMessage = (String) model.getAttribute("noResultsMessage");
        assertTrue(noResultsMessage.contains("Arrr!"));
        assertTrue(noResultsMessage.contains("No treasure found"));
    }

    @Test
    @DisplayName("Should search with multiple criteria")
    public void testSearchMovies_MultipleCriteria_ReturnsMatchingResults() {
        String result = moviesController.searchMovies("Test", 1L, "Drama", model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchName"));
        assertTrue(model.containsAttribute("searchId"));
        assertTrue(model.containsAttribute("searchGenre"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        
        Movie movie = movies.get(0);
        assertEquals("Test Movie", movie.getMovieName());
        assertEquals(1L, movie.getId());
        assertEquals("Drama", movie.getGenre());
    }

    @Test
    @DisplayName("Should include genres in all search responses")
    public void testSearchMovies_AlwaysIncludesGenres() {
        String result = moviesController.searchMovies("Test", null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("genres"));
        
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) model.getAttribute("genres");
        assertNotNull(genres);
        assertEquals(2, genres.size());
    }

    @Test
    @DisplayName("Should handle search with empty string parameters")
    public void testSearchMovies_EmptyStringParameters_TreatedAsNull() {
        String result = moviesController.searchMovies("", null, "", model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size()); // Should return all movies like no criteria
    }

    @Test
    @DisplayName("Should preserve search parameters in model for form persistence")
    public void testSearchMovies_PreservesSearchParameters() {
        String testName = "Test";
        Long testId = 1L;
        String testGenre = "Drama";
        
        String result = moviesController.searchMovies(testName, testId, testGenre, model);
        
        assertEquals("movies", result);
        assertEquals(testName, model.getAttribute("searchName"));
        assertEquals(testId, model.getAttribute("searchId"));
        assertEquals(testGenre, model.getAttribute("searchGenre"));
    }

    @Test
    @DisplayName("Should handle service integration correctly")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(2, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        assertEquals("Action Movie", movies.get(1).getMovieName());
    }
}