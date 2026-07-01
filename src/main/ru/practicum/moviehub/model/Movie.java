package ru.practicum.moviehub.model;

public class Movie {
    private final Long id;
    private final String name;
    private final Integer  year;

    public Movie(Long id, Integer year, String name) {
        this.id = id;
        this.year = year;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Integer getYear() {
        return year;
    }

    public String getName() {
        return name;
    }
}