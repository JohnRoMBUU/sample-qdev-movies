package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        return "movies";
    }

    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Searching movies with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                logger.warn("Invalid movie ID provided: {}", id);
                model.addAttribute("movies", List.of());
                model.addAttribute("genres", movieService.getAllGenres());
                model.addAttribute("searchPerformed", true);
                model.addAttribute("searchName", name);
                model.addAttribute("searchId", null); // Don't show invalid ID
                model.addAttribute("searchGenre", genre);
                model.addAttribute("errorMessage", "Shiver me timbers! That ID be not a valid treasure map number, matey!");
                return "movies";
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            model.addAttribute("movies", searchResults);
            model.addAttribute("genres", movieService.getAllGenres());
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            
            // Add pirate-themed messages based on results
            if (searchResults.isEmpty()) {
                boolean hasSearchCriteria = (name != null && !name.trim().isEmpty()) || 
                                          id != null || 
                                          (genre != null && !genre.trim().isEmpty());
                if (hasSearchCriteria) {
                    model.addAttribute("noResultsMessage", "Arrr! No treasure found with those search terms, ye scurvy dog! Try different criteria to find yer movie bounty!");
                }
            } else {
                String treasureMessage = searchResults.size() == 1 ? 
                    "Ahoy! Found 1 movie treasure for ye, matey!" :
                    "Shiver me timbers! Found " + searchResults.size() + " movie treasures in our chest!";
                model.addAttribute("successMessage", treasureMessage);
            }
            
            logger.info("Search completed. Found {} movies", searchResults.size());
            return "movies";
            
        } catch (Exception e) {
            logger.error("Error occurred during movie search: {}", e.getMessage(), e);
            model.addAttribute("movies", List.of());
            model.addAttribute("genres", movieService.getAllGenres());
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            model.addAttribute("errorMessage", "Batten down the hatches! Something went wrong with the search, ye landlubber! Try again later.");
            return "movies";
        }
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Arrr! That movie treasure with ID " + movieId + " has sailed away to Davy Jones' locker, matey!");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}