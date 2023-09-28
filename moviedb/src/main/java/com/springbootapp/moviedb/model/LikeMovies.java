package com.springbootapp.moviedb.model;

import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
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

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public void setUser(User user) {
        this.user = user;
        this.userLogin = user.getLogin();
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        this.movieId = movie.getId();
    }
}