package com.wq1019.test.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.wq1019.test.models.User;
import com.wq1019.test.services.UserService;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/users", produces = "application/json;charset=UTF-8")
public class UserController extends BaseController {

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
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setCreated_at(timestamp.toString());
        user.setUpdated_at(timestamp.toString());
        userService.store(user);
    }

    @DeleteMapping(value = "/delete/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("user_id") Long user_id) {
        userService.delete(user_id);
    }

    @GetMapping(value = "/detail/{user_id}")
    @ResponseBody
    public User detail(@PathVariable("user_id") Long user_id) {
        return userService.findById(user_id);
    }

    @RequestMapping(value = "/index")
    @ResponseBody
    public Map<String, Object> query(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int limit) {

        page = (page < 1) ? 1 : page;
        int offset = (page - 1) * limit;
        // 排序輸出
        Example e = new Example(User.class);
        e.orderBy("updated_at").desc();
        List<User> users = userService.pagination(e, offset, limit);
        // 組裝數據
        Map<String, Object> userMapping = new HashMap<>();
        userMapping.put("data", users);// 組裝 data
        userMapping.put("meta", userService.addPaginateMeta(page, limit));// 組裝 meta
        return userMapping;
    }

    @RequestMapping(value = "/show")
    public String index() {
        return "/user/index";
    }

}