package com.springbootapp.moviedb.model;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "Логин пользователя, оставившего комментарий")
    private String userLogin;

    @Column(name = "movie_id")
    @ApiModelProperty(value = "Идентификатор фильма")
    private Integer movieId;

    @Column(name = "comment")
    @ApiModelProperty(value = "Текст комментария к фильму")
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