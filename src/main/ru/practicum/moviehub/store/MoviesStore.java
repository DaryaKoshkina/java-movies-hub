package ru.practicum.moviehub.store;
import ru.practicum.moviehub.model.Movie;

import java.util.*;

public class MoviesStore {
    private final Map<String, Movie> movies = new HashMap<>();

    public Movie save(Movie movie) {
        if (movie.getId() == null) {
            movie.setId(UUID.randomUUID().toString());
        }
        movies.put(movie.getId(), movie);
        return movie;
    }

    public Movie findById(String id) {
        return movies.get(id);
    }

    public List<Movie> findAll() {
        return new ArrayList<>(movies.values());
    }

    public boolean delete(String id) {
        return movies.remove(id) != null;
    }

    public void clear() {
        movies.clear();
    }
}