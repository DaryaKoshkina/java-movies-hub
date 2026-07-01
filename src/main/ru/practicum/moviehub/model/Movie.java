package ru.practicum.moviehub.model;

public class Movie {
    private String id;
    private final String title;
    private final Integer  year;

    public Movie(String id, Integer year, String title) {
        this.id = id;
        this.year = year;
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}