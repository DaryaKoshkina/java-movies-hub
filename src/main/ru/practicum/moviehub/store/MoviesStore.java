package ru.practicum.moviehub.store;
import ru.practicum.moviehub.model.Movie;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MoviesStore {
    private final Map<Long, Movie> movies = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Movie save(Movie movie) {
        if (movie.getId() == null) {
            movie.setId(idCounter.getAndIncrement());
        }
        movies.put(movie.getId(), movie);
        return movie;
    }

    public Movie findById(Long id) {
        return movies.get(id);
    }

    public List<Movie> findAll() {
        return new ArrayList<>(movies.values());
    }

    public boolean delete(Long id) {
        return movies.remove(id) != null;
    }

    public void clear() {
        movies.clear();
    }
}