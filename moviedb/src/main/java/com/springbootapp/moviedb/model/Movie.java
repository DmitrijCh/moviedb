package com.springbootapp.moviedb.model;

import javax.persistence.*;

@Entity
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

    public Movie() {
    }

    public Movie(Integer id, String originalName, String name, String year, String time, String ageRestriction, String description,
                 String slogan, String budget, String countryRu, String type, String createdAt, String updatedAt, String poster) {
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
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getCountryRu() {
        return countryRu;
    }

    public void setCountryRu(String countryRu) {
        this.countryRu = countryRu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}

