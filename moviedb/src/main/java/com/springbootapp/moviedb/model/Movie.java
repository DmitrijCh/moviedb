package com.springbootapp.moviedb.model;

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
    private Integer id;

    @Column(name = "name_original")
    private String originalName;

    @Column(name = "name")
    private String name;

    @Column(name = "year")
    private String year;

    @Column(name = "time")
    private String time;

    @Column(name = "age_restriction")
    private String ageRestriction;

    @Column(name = "description")
    private String description;

    @Column(name = "slogan")
    private String slogan;

    @Column(name = "budget")
    private String budget;

    @Column(name = "country_ru")
    private String countryRu;

    @Column(name = "type")
    private String type;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "poster")
    private String poster;

    @Column(name = "persons")
    private String persons;

    @Column(name = "genres")
    private String genres;

    public Movie() {
    }

    public Movie(Integer id, String originalName, String name, String year, String time, String ageRestriction, String description,
                 String slogan, String budget, String countryRu, String type, String createdAt, String updatedAt, String poster,
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
        this.countryRu = countryRu;
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

    public void setCountryRu(String countryRu) {
        this.countryRu = countryRu;
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