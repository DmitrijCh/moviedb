package com.springbootapp.moviedb.service.impl;

import com.springbootapp.moviedb.dao.impl.UserDaoImpl;
import com.springbootapp.moviedb.entity.User;
import com.springbootapp.moviedb.service.UserService;
import org.springframework.util.DigestUtils;

public class UserServiceImpl implements UserService {
    UserDaoImpl userDao = new UserDaoImpl();

    @Override
    public String login(User user) {
        User findUser = userDao.findByLogin(user.getLogin());
        if (findUser != null) {
            if (DigestUtils.md5DigestAsHex((user.getPassword()).getBytes()).equals(findUser.getPassword())) {
                return "life is beautiful," + "your Id: " + findUser.getId();
            }
        }
        return "do not give up";
    }

    @Override
    public String registration(User user) {
        User findUser = userDao.findByLogin(user.getLogin());
        if (findUser == null) {
            userDao.save(user);
            return "life is beautiful";
        }
        return "this login is not available";
    }
}
