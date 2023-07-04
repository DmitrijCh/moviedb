package com.springbootapp.moviedb.model;

import javax.persistence.*;

@Entity
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
