package com.springbootapp.moviedb.entity;

public class Table {
    public int id;
    public String title;
    public String posterPath;

    public Table() {
    }

    public Table(int id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
    }

    public int getId(Table table) {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle(Table table) {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath(Table table) {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id: " + id + ", title: " + title + ", poster_path: " + posterPath + ")";
    }
}
