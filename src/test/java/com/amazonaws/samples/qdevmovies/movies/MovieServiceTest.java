package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MovieService search functionality.
 * Arrr! These tests be making sure our treasure hunting methods work properly!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    public void testSearchMovies_NoSearchCriteria_ReturnsAllMovies() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertEquals(allMovies.size(), results.size());
        assertTrue(results.containsAll(allMovies));
    }

    @Test
    @DisplayName("Should return all movies when empty search criteria provided")
    public void testSearchMovies_EmptySearchCriteria_ReturnsAllMovies() {
        List<Movie> results = movieService.searchMovies("", null, "");
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertEquals(allMovies.size(), results.size());
        assertTrue(results.containsAll(allMovies));
    }

    @Test
    @DisplayName("Should find movies by exact name match")
    public void testSearchMovies_ExactNameMatch_ReturnsCorrectMovie() {
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should find movies by partial name match (case insensitive)")
    public void testSearchMovies_PartialNameMatch_ReturnsCorrectMovies() {
        List<Movie> results = movieService.searchMovies("the", null, null);
        
        assertTrue(results.size() > 0);
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    @DisplayName("Should find movie by exact ID")
    public void testSearchMovies_ExactIdMatch_ReturnsCorrectMovie() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    @DisplayName("Should find movies by genre (case insensitive)")
    public void testSearchMovies_GenreMatch_ReturnsCorrectMovies() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        
        assertTrue(results.size() > 0);
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should find movies by partial genre match")
    public void testSearchMovies_PartialGenreMatch_ReturnsCorrectMovies() {
        List<Movie> results = movieService.searchMovies(null, null, "sci");
        
        assertTrue(results.size() > 0);
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("sci"));
        }
    }

    @Test
    @DisplayName("Should find movies matching multiple criteria")
    public void testSearchMovies_MultipleCriteria_ReturnsCorrectMovies() {
        List<Movie> results = movieService.searchMovies("the", null, "drama");
        
        assertTrue(results.size() > 0);
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should return empty list when no movies match criteria")
    public void testSearchMovies_NoMatches_ReturnsEmptyList() {
        List<Movie> results = movieService.searchMovies("NonexistentMovie", null, null);
        
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when invalid ID provided")
    public void testSearchMovies_InvalidId_ReturnsEmptyList() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle whitespace in search terms")
    public void testSearchMovies_WhitespaceInSearchTerms_HandlesCorrectly() {
        List<Movie> results = movieService.searchMovies("  the  ", null, "  drama  ");
        
        assertTrue(results.size() > 0);
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should return all unique genres")
    public void testGetAllGenres_ReturnsUniqueGenres() {
        List<String> genres = movieService.getAllGenres();
        
        assertNotNull(genres);
        assertTrue(genres.size() > 0);
        
        // Check that genres are unique
        assertEquals(genres.size(), genres.stream().distinct().count());
        
        // Check that genres are sorted
        List<String> sortedGenres = genres.stream().sorted().collect(java.util.stream.Collectors.toList());
        assertEquals(sortedGenres, genres);
    }

    @Test
    @DisplayName("Should get movie by valid ID")
    public void testGetMovieById_ValidId_ReturnsMovie() {
        Optional<Movie> result = movieService.getMovieById(1L);
        
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Should return empty optional for invalid ID")
    public void testGetMovieById_InvalidId_ReturnsEmpty() {
        Optional<Movie> result = movieService.getMovieById(999L);
        
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty optional for null ID")
    public void testGetMovieById_NullId_ReturnsEmpty() {
        Optional<Movie> result = movieService.getMovieById(null);
        
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty optional for zero or negative ID")
    public void testGetMovieById_ZeroOrNegativeId_ReturnsEmpty() {
        Optional<Movie> result1 = movieService.getMovieById(0L);
        Optional<Movie> result2 = movieService.getMovieById(-1L);
        
        assertFalse(result1.isPresent());
        assertFalse(result2.isPresent());
    }

    @Test
    @DisplayName("Should return all movies from getAllMovies")
    public void testGetAllMovies_ReturnsAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        
        assertNotNull(movies);
        assertTrue(movies.size() > 0);
        
        // Verify that we have the expected test movies
        boolean hasExpectedMovie = movies.stream()
                .anyMatch(movie -> "The Prison Escape".equals(movie.getMovieName()));
        assertTrue(hasExpectedMovie);
    }
}