package com.springbootapp.moviedb.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "like_movies")
public class LikeMovies implements Serializable {
    @Id
    @Column(name = "user_login")
    private String userLogin;

    @Id
    @Column(name = "movie_id")
    private Integer movieId;

    @ManyToOne
    @JoinColumn(name = "user_login", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    private Movie movie;

    public LikeMovies() {
    }

    public LikeMovies(User user, Integer movieId) {
        this.user = user;
        this.userLogin = user.getLogin();
        this.movieId = movieId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userLogin = user.getLogin();
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        this.movieId = movie.getId();
    }
}





