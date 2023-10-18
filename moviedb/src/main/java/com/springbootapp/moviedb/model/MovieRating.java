package com.springbootapp.moviedb.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "rating_movies")
public class MovieRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    @ApiModelProperty(value = "Уникальный идентификатор фильма")
    private Integer id;

    @Column(name = "user_login")
    @ApiModelProperty(value = "Логин пользователя, оставившего рейтинг")
    private String name;

    @Column(name = "rating")
    @ApiModelProperty(value = "Рейтинг фильма, представленный пользователем")
    private String rating;

    public MovieRating() {
    }

    public MovieRating(Integer id, String name, String rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}