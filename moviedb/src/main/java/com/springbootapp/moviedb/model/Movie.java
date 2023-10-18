package com.springbootapp.moviedb.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "movies")
public class Movie {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "Уникальный идентификатор фильма")
    private Integer id;

    @Column(name = "name_original")
    @ApiModelProperty(value = "Оригинальное название фильма")
    private String originalName;

    @Column(name = "name")
    @ApiModelProperty(value = "Название фильма")
    private String name;

    @Column(name = "year")
    @ApiModelProperty(value = "Год выпуска фильма")
    private String year;

    @Column(name = "time")
    @ApiModelProperty(value = "Длительность фильма")
    private String time;

    @Column(name = "age_restriction")
    @ApiModelProperty(value = "Возрастное ограничение фильма")
    private String ageRestriction;

    @Column(name = "description")
    @ApiModelProperty(value = "Описание фильма")
    private String description;

    @Column(name = "slogan")
    @ApiModelProperty(value = "Слоган фильма")
    private String slogan;

    @Column(name = "budget")
    @ApiModelProperty(value = "Бюджет фильма")
    private String budget;

    @Column(name = "country_ru")
    @ApiModelProperty(value = "Страна производства фильма")
    private String country;

    @Column(name = "type")
    @ApiModelProperty(value = "Тип фильма")
    private String type;

    @Column(name = "created_at")
    @ApiModelProperty(value = "Дата создания записи о фильме")
    private String createdAt;

    @Column(name = "updated_at")
    @ApiModelProperty(value = "Дата обновления записи о фильме")
    private String updatedAt;

    @Column(name = "poster")
    @ApiModelProperty(value = "Ссылка на постер фильма")
    private String poster;

    @Column(name = "persons")
    @ApiModelProperty(value = "Список актеров и персонала, связанных с фильмом")
    private String persons;

    @Column(name = "genres")
    @ApiModelProperty(value = "Список жанров фильма")
    private String genres;

    public Movie() {
    }

    public Movie(Integer id, String originalName, String name, String year, String time, String ageRestriction, String description,
                 String slogan, String budget, String country, String type, String createdAt, String updatedAt, String poster,
                 String persons, String genres) {
        this.id = id;
        this.originalName = originalName;
        this.name = name;
        this.year = year;
        this.time = time;
        this.ageRestriction = ageRestriction;
        this.description = description;
        this.slogan = slogan;
        this.budget = budget;
        this.country = country;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.poster = poster;
        this.persons = persons;
        this.genres = genres;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setPersons(String persons) {
        this.persons = persons;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}