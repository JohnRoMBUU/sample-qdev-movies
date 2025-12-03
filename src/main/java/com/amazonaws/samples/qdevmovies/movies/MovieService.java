package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Search movies based on provided criteria.
     * Arrr! This method be the treasure hunter that finds movies matching yer search criteria!
     * 
     * @param name Movie name to search for (case-insensitive partial match)
     * @param id Movie ID to search for (exact match)
     * @param genre Movie genre to search for (case-insensitive partial match)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Searching for movies with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = new ArrayList<>();
        
        // If all parameters are null or empty, return all movies
        if (isEmptySearchCriteria(name, id, genre)) {
            logger.info("No search criteria provided, returning all movies");
            return new ArrayList<>(movies);
        }
        
        for (Movie movie : movies) {
            if (matchesSearchCriteria(movie, name, id, genre)) {
                results.add(movie);
            }
        }
        
        logger.info("Found {} movies matching search criteria", results.size());
        return results;
    }

    /**
     * Check if all search criteria are empty or null
     */
    private boolean isEmptySearchCriteria(String name, Long id, String genre) {
        return (name == null || name.trim().isEmpty()) && 
               id == null && 
               (genre == null || genre.trim().isEmpty());
    }

    /**
     * Check if a movie matches the provided search criteria
     */
    private boolean matchesSearchCriteria(Movie movie, String name, Long id, String genre) {
        // Check ID match (exact match if provided)
        if (id != null && !movie.getId().equals(id)) {
            return false;
        }
        
        // Check name match (case-insensitive partial match if provided)
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            String movieName = movie.getMovieName().toLowerCase();
            if (!movieName.contains(searchName)) {
                return false;
            }
        }
        
        // Check genre match (case-insensitive partial match if provided)
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            String movieGenre = movie.getGenre().toLowerCase();
            if (!movieGenre.contains(searchGenre)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get all unique genres from the movie collection.
     * Useful for populating genre dropdown in search forms.
     * 
     * @return List of unique genres
     */
    public List<String> getAllGenres() {
        return movies.stream()
                .map(Movie::getGenre)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
    }
}
