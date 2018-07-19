package com.wq1019.test.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.wq1019.test.user.model.User;
import com.wq1019.test.user.service.UserService;

import java.util.Map;

@Controller
@RequestMapping(value = "/users", produces = "application/json;charset=UTF-8")
public class UserController {

    private UserService userService;

    private static final String DEFAULT_PAGE_SIZE = "5";

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    /**
     * 添加用戶信息
     */
    @PostMapping(value = "/store")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void store(@RequestBody User user) {
        userService.store(user);
    }

    @PutMapping(value = "/update/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody User data, @PathVariable("user_id") int user_id) {
        userService.update(user_id, data);
    }

    @DeleteMapping(value = "/delete/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("user_id") int user_id) {
        userService.delete(user_id);
    }

    @GetMapping(value = "/detail/{user_id}")
    @ResponseBody
    public User detail(@PathVariable("user_id") int user_id) {
        return userService.detail(user_id);
    }

    @RequestMapping(value = "/index")
    @ResponseBody
    public Map<String, Object> query(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int limit) {
        return userService.query(page, limit);
    }

    @RequestMapping(value = "/show")
    public String index() {
        return "/user/index";
    }

}