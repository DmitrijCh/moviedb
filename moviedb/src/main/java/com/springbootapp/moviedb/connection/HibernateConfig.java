package com.springbootapp.moviedb.connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

@Component
public class HibernateConfig {
    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure("/hibernate.cfg.xml").buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Ошибка при создании SessionFactory: " + e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }
}





