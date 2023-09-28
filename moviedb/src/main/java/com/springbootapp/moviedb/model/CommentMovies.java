package com.springbootapp.moviedb.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "comment_movies")
public class CommentMovies {

    @Id
    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "comment")
    private String comment;

    public CommentMovies() {
    }

    public CommentMovies(String userLogin, Integer movieId, String comment) {
        this.userLogin = userLogin;
        this.movieId = movieId;
        this.comment = comment;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}