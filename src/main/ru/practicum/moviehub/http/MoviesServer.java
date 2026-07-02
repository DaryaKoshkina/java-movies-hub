package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.api.ErrorResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MoviesServer {
    private final HttpServer server;

    public MoviesServer(MoviesStore store, int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);


        this.server.createContext("/movies", new MoviesHandler(store));
        this.server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

    static class MoviesHandler extends BaseHttpHandler {
        private final MoviesStore store;
        private final Gson gson = new Gson();

        public MoviesHandler(MoviesStore store) {
            this.store = store;
        }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            try {
                String method = ex.getRequestMethod().toUpperCase();
                String path = ex.getRequestURI().getPath();
                String query = ex.getRequestURI().getQuery();

                if ("/movies".equals(path) || "/movies/".equals(path)) {
                    switch (method) {
                        case "GET":
                            if (query != null && query.startsWith("year=")) {
                                try {
                                    int year = Integer.parseInt(query.substring(5));
                                    List<Movie> filtered = new ArrayList<>();
                                    for (Movie m : store.findAll()) {
                                        if (m.getYear() != null && m.getYear() == year) {
                                            filtered.add(m);
                                        }
                                    }
                                    sendJson(ex, 200, gson.toJson(filtered));
                                } catch (NumberFormatException e) {
                                    sendJson(ex, 400, gson.toJson(new ErrorResponse("Некорректный параметр запроса — 'year'")));
                                }
                            } else {
                                sendJson(ex, 200, gson.toJson(store.findAll()));
                            }
                            break;

                        case "POST":
                            String ct = ex.getRequestHeaders().getFirst("Content-Type");
                            if (ct == null || !ct.toLowerCase().startsWith("application/json")) {
                                ex.sendResponseHeaders(415, -1);
                                return;
                            }

                            try (InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)) {
                                Movie movie = gson.fromJson(reader, Movie.class);
                                List<String> details = new ArrayList<>();

                                if (movie == null) {
                                    details.add("Тело запроса не должно быть пустым");
                                } else {
                                    if (movie.getTitle() == null || movie.getTitle().isBlank()) {
                                        details.add("название не должно быть пустым");
                                    } else if (movie.getTitle().length() > 100) {
                                        details.add("длина названия не должна превышать 100 символов");
                                    }
                                    if (movie.getYear() == null || movie.getYear() < 1888 || movie.getYear() > 2027) {
                                        details.add("год должен быть между 1888 и 2027");
                                    }
                                }

                                if (!details.isEmpty()) {
                                    sendJson(ex, 422, gson.toJson(new ErrorResponse("Ошибка валидации", details)));
                                } else {
                                    sendJson(ex, 201, gson.toJson(store.save(movie)));
                                }
                            } catch (Exception e) {
                                sendJson(ex, 400, gson.toJson(new ErrorResponse("Некорректный JSON")));
                            }
                            break;

                        default:
                            ex.sendResponseHeaders(405, -1);
                            break;
                    }
                }
                else if (path.startsWith("/movies/")) {
                    Long idParam;
                    try {
                        idParam = Long.parseLong(path.substring(8));
                    } catch (NumberFormatException e) {
                        sendJson(ex, 400, gson.toJson(new ErrorResponse("Некорректный ID")));
                        return;
                    }

                    switch (method) {
                        case "GET":
                            Movie movie = store.findById(idParam);
                            if (movie != null) {
                                sendJson(ex, 200, gson.toJson(movie));
                            } else {
                                sendJson(ex, 404, gson.toJson(new ErrorResponse("Фильм не найден")));
                            }
                            break;

                        case "DELETE":
                            if (store.delete(idParam)) {
                                sendNoContent(ex);
                            } else {
                                sendJson(ex, 404, gson.toJson(new ErrorResponse("Фильм не найден")));
                            }
                            break;

                        default:
                            ex.sendResponseHeaders(405, -1);
                            break;
                    }
                } else {
                    sendJson(ex, 404, gson.toJson(new ErrorResponse("Ресурс не найден")));
                }
            } catch (Exception e) {
                ex.sendResponseHeaders(500, -1);
            } finally {
                ex.close();
            }
        }
    }
}