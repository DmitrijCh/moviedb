package com.springbootapp.moviedb.dao.impl;

import com.springbootapp.moviedb.dao.UserDao;
import com.springbootapp.moviedb.entity.User;
import org.springframework.util.DigestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {


    @Override
    public User findByLogin(String login)  {
        Connection cm = new Connection();
        Connection con = cm.getConnection();
        User user = null;
        if (con != null) {
            try {
                PreparedStatement pr = con.prepareStatement("SELECT * FROM regUser where LOGIN=?");
                pr.setString(1,login);
                ResultSet resultSet = pr.executeQuery();//return sql result
                if(resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("ID"));
                    user.setName(resultSet.getString("NAME"));
                    user.setSurname(resultSet.getString("USERNAME"));
                    user.setLogin(login);
                    user.setPassword(resultSet.getString("PASSWORD"));
                    return user;
                }
                pr.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public Boolean save(User user) {
        Connection cm = new Connection();
        Connection con = cm.getConnection();
        if (con != null) {
            try {
                PreparedStatement pr = con.prepareStatement("insert into regUser (NAME,USERNAME,LOGIN,PASSWORD) values (?,?,?,?)");
                pr.setString(1,user.getName());
                pr.setString(2,user.getSurname());
                pr.setString(3,user.getLogin());
                pr.setString(4, DigestUtils.md5DigestAsHex((user.getPassword()).getBytes()));
                pr.executeUpdate();
                pr.close();
                con.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
