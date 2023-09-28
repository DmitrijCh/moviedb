package com.springbootapp.moviedb.connection;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ConnectionManager {

    protected final JdbcTemplate jdbcTemplate;
    protected final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public ConnectionManager(DataSource dataSource, SqlSessionFactory sqlSessionFactory) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.sqlSessionFactory = sqlSessionFactory;
    }
}