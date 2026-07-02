package ru.practicum.moviehub.model;

public class Movie {
    private Long id;
    private final String title;
    private final Integer  year;

    public Movie(Long id, String title, Integer year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    public Integer getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}