package com.springbootapp.moviedb.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootapp.moviedb.connection.HibernateConfig;
import com.springbootapp.moviedb.model.*;
import com.springbootapp.moviedb.token.Timestamp;
import com.springbootapp.moviedb.token.TokenUsers;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Qualifier("hibernate")
public class HibernateStorage implements Storage {

    private final HibernateConfig hibernateConfig;
    private final TokenUsers tokenUsers;
    private final Timestamp timestamp;

    public HibernateStorage(HibernateConfig hibernateConfig, TokenUsers tokenUsers, Timestamp timestamp) {
        this.hibernateConfig = hibernateConfig;
        this.tokenUsers = tokenUsers;
        this.timestamp = timestamp;
    }

    public SessionFactory getSessionFactory() {
        return hibernateConfig.getSessionFactory();
    }

    public EntityManager getEntityManager() {
        return getSessionFactory().createEntityManager();
    }

    @Override
    public void addUsers(String name, String login, String password) {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setPassword(password);
        getEntityManager().persist(user);
    }

    @Override
    public void addSessionKeys(String key, String login, String timestamp) {
        Key sessionKey = new Key();
        sessionKey.setKey(key);
        sessionKey.setUserLogin(login);
        sessionKey.setTimestamp(timestamp);
        getEntityManager().persist(sessionKey);
    }

    @Override
    public String registration(String login) {
        String key = tokenUsers.getKey();
        String stamp = timestamp.getStamp();
        addSessionKeys(key, login, stamp);
        Key keys = new Key();
        keys.setKey(key);
        try {
            return new ObjectMapper().writeValueAsString(keys);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUser(String key) {
        Key sessionKey = getEntityManager().find(Key.class, key);
        if (sessionKey != null) {
            return sessionKey.getUser();
        }
        return null;
    }

    @Override
    public String searchKey(User user) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root.get("name"));
        criteriaQuery.where(criteriaBuilder.equal(root, user));
        TypedQuery<String> query = getEntityManager().createQuery(criteriaQuery);
        return query.getSingleResult();
    }


    @Override
    public String verificationKey(String login, String password) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<Key> root = criteriaQuery.from(Key.class);
        Join<Key, User> userJoin = root.join("user");
        criteriaQuery.select(root.get("key"));
        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(userJoin.get("login"), login),
                        criteriaBuilder.equal(userJoin.get("password"), password)
                )
        );
        TypedQuery<String> query = getEntityManager().createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    @Override
    public List<Movie> getMovie(User user, int count, int offset, String type) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Movie> criteriaQuery = criteriaBuilder.createQuery(Movie.class);
        Root<Movie> root = criteriaQuery.from(Movie.class);

        List<Predicate> predicates = new ArrayList<>();

        Subquery<String> sub = criteriaQuery.subquery(String.class);
        Root<Key> sessionKeyRoot = sub.from(Key.class);
        sub.select(sessionKeyRoot.get("user").get("login"))
                .where(criteriaBuilder.equal(sessionKeyRoot.get("user").get("login"), user.getLogin()));

        predicates.add(criteriaBuilder.exists(sub));

        if (!"all".equals(type)) {
            predicates.add(criteriaBuilder.equal(root.get("type"), type));
        }

        criteriaQuery.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(criteriaBuilder.asc(root.get("id")));

        TypedQuery<Movie> query = getEntityManager().createQuery(criteriaQuery)
                .setMaxResults(count)
                .setFirstResult(offset);

        return query.getResultList();
    }

    @Override
    public List<Movie> searchMovies(String name) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Movie> criteriaQuery = criteriaBuilder.createQuery(Movie.class);
        Root<Movie> root = criteriaQuery.from(Movie.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
        ));
        TypedQuery<Movie> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<Movie> getLikeMovie(User user, int movieId) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            Movie movie = entityManager.find(Movie.class, movieId);
            if (movie != null) {
                LikeMovies likeMovie = new LikeMovies();
                likeMovie.setUser(user);
                likeMovie.setMovie(movie);
                entityManager.persist(likeMovie);
            }

            transaction.commit();

            if (movie != null) {
                return Collections.singletonList(movie);
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public List<Movie> deleteLikeMovie(User user, int movieId) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            LikeMovies likeMovies = new LikeMovies();
            likeMovies.setUserLogin(user.getLogin());
            likeMovies.setMovieId(movieId);

            entityManager.remove(likeMovies);

            transaction.commit();

            List<Movie> movies = new ArrayList<>();
            Movie movie = new Movie();
            movie.setId(movieId);
            movies.add(movie);
            return movies;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public List<Movie> getFavoriteMovies(User user) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Movie> criteriaQuery = criteriaBuilder.createQuery(Movie.class);
        Root<LikeMovies> likeMoviesRoot = criteriaQuery.from(LikeMovies.class);
        criteriaQuery.select(likeMoviesRoot.get("movie"));

        Predicate predicate = criteriaBuilder.equal(likeMoviesRoot.get("userLogin"), user.getLogin());
        criteriaQuery.where(predicate);

        TypedQuery<Movie> query = entityManager.createQuery(criteriaQuery);
        List<Movie> favoriteMovies = query.getResultList();

        entityManager.getTransaction().commit();

        return favoriteMovies;
    }

    @Override
    public List<Movie> getLikeAndDislikeMovie(User user) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Movie> criteriaQuery = criteriaBuilder.createQuery(Movie.class);
        Root<LikeMovies> likeMoviesRoot = criteriaQuery.from(LikeMovies.class);
        criteriaQuery.select(likeMoviesRoot.get("movie"));

        Predicate predicate = criteriaBuilder.equal(likeMoviesRoot.get("userLogin"), user.getLogin());
        criteriaQuery.where(predicate);

        TypedQuery<Movie> query = entityManager.createQuery(criteriaQuery);
        List<Movie> favoriteMovies = query.getResultList();

        entityManager.getTransaction().commit();

        return favoriteMovies;
    }

    @Override
    public int addRating(User user, int movieId, String rating) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction transaction = null;
        int count = 0;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
            Root<MovieRating> root = countQuery.from(MovieRating.class);
            countQuery.select(builder.count(root));
            countQuery.where(builder.and(
                    builder.equal(root.get("id"), movieId),
                    builder.equal(root.get("name"), user.getLogin())
            ));
            count = entityManager.createQuery(countQuery).getSingleResult().intValue();

            if (count > 0) {
                CriteriaUpdate<MovieRating> updateQuery = builder.createCriteriaUpdate(MovieRating.class);
                Root<MovieRating> updateRoot = updateQuery.from(MovieRating.class);
                updateQuery.set("rating", rating);
                updateQuery.where(builder.and(
                        builder.equal(updateRoot.get("id"), movieId),
                        builder.equal(updateRoot.get("name"), user.getLogin())
                ));
                entityManager.createQuery(updateQuery).executeUpdate();
            } else {
                MovieRating ratingMovie = new MovieRating();
                ratingMovie.setId(movieId);
                ratingMovie.setName(user.getLogin());
                ratingMovie.setRating(rating);
                entityManager.merge(ratingMovie);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return count;
    }

    @Override
    public List<MovieRating> getUserRating(User user) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MovieRating> criteriaQuery = criteriaBuilder.createQuery(MovieRating.class);
        Root<MovieRating> root = criteriaQuery.from(MovieRating.class);

        criteriaQuery.select(criteriaBuilder.construct(MovieRating.class,
                        root.get("id"),
                        root.get("name"),
                        root.get("rating")))
                .where(criteriaBuilder.equal(root.get("name"), user.getLogin()));

        List<MovieRating> result = entityManager.createQuery(criteriaQuery).getResultList();
        entityManager.close();

        return result;
    }

    @Override
    public List<MovieRating> getOverallRating() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<MovieRating> root = criteriaQuery.from(MovieRating.class);

        Expression<Double> avgRatingExpression = criteriaBuilder.avg(root.get("rating").as(Double.class));
        criteriaQuery.multiselect(root.get("id"), avgRatingExpression.alias("averageRating"));
        criteriaQuery.groupBy(root.get("id"));

        List<Object[]> results = entityManager.createQuery(criteriaQuery).getResultList();

        List<MovieRating> overallRatingList = new ArrayList<>();

        for (Object[] result : results) {
            MovieRating movieRating = new MovieRating();
            movieRating.setId((Integer) result[0]);
            double avgRating = (Double) result[1];
            movieRating.setRating(Double.toString(avgRating));
            overallRatingList.add(movieRating);
        }

        entityManager.close();

        return overallRatingList;
    }

    @Override
    public void addCommentMovie(User user, int movieId, String comment) {
        Session session = getEntityManager().unwrap(Session.class);
        session.beginTransaction();

        CommentMovies commentMovie = new CommentMovies(user.getLogin(), movieId, comment);
        session.persist(commentMovie);

        session.getTransaction().commit();
    }

    @Override
    public List<CommentMovies> getOverallComments(int movieId) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CommentMovies> query = builder.createQuery(CommentMovies.class);
        Root<CommentMovies> root = query.from(CommentMovies.class);

        Subquery<String> subquery = query.subquery(String.class);
        Root<User> userRoot = subquery.from(User.class);
        subquery.select(userRoot.get("name"))
                .where(builder.equal(userRoot.get("login"), root.get("userLogin")));

        query.select(builder.construct(
                        CommentMovies.class,
                        subquery.getSelection().alias("userName"),
                        root.get("movieId"),
                        root.get("comment")
                ))
                .where(builder.equal(root.get("movieId"), movieId))
                .orderBy(builder.asc(root.get("movieId")));

        return getEntityManager().createQuery(query).getResultList();
    }

    @Override
    public String searchLogin(User user) {
        Session session = getEntityManager().unwrap(Session.class);

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.select(root.get("login"))
                .where(criteriaBuilder.equal(root.get("login"), user.getLogin()));

        javax.persistence.Query query = session.createQuery(criteriaQuery);
        return (String) query.getSingleResult();
    }
}








