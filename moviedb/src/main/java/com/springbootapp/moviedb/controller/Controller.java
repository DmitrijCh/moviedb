package com.springbootapp.moviedb.controller;

import com.springbootapp.moviedb.entity.Table;
import com.springbootapp.moviedb.storage.TaskStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class Controller {
    private final TaskStorage taskStorage;
    UserServiceImpl userService = new UserServiceImpl();

    @RequestMapping("/")
    String main() {
        return "Hello from Controller";
    }


    @RequestMapping("/loginForm")
    ModelAndView loginForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("loginForm.html");
        return modelAndView;
    }

    @RequestMapping("/registrationForm")
    ModelAndView registrationForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registrationForm.html");
        return modelAndView;
    }

    @PostMapping("/login")
    String login(@RequestParam String login, @RequestParam String password) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        return userService.login(user);
    }

    @PostMapping("/registration")
    String registration(@RequestParam String name,@RequestParam String username, @RequestParam String login,@RequestParam String password) {
        User user = new User();
        user.setName(name);
        user.setSurname(username);
        user.setLogin(login);
        user.setPassword(password);
        return userService.registration(user);
    }

    @Autowired
    public Controller(TaskStorage taskStorage) {
        this.taskStorage = taskStorage;
    }

    @GetMapping("/tasks")
    public List<Table> listTasks() {
        return taskStorage.getTasks();
    }

//    @PostMapping("/tasks")
//    public ApiResponse addTask(@RequestParam Table table) {
//        taskStorage.addTask(table);
//        return ApiResponse.ok();
//    }
}