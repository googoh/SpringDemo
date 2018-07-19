package com.wq1019.test.user.service;

import com.wq1019.test.user.repositories.UserRepostitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wq1019.test.user.model.User;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private UserRepostitory userRepostitory = null;

    public UserService(@Autowired UserRepostitory userRepostitory) {
        this.userRepostitory = userRepostitory;
    }

    public int store(User user) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setCreated_at(timestamp.toString());
        user.setUpdated_at(timestamp.toString());

        return userRepostitory.store(user);
    }

    public int update(int user_id, User data) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        data.setUpdated_at(timestamp.toString());
        return userRepostitory.save(user_id, data);
    }

    public int delete(int id) {
        return userRepostitory.delete(id);
    }

    public Map<String, Object> query(int page, int limit) {
        page = (page < 1) ? 1 : page;
        int offset = (page - 1) * limit;
        List<User> users = userRepostitory.query(offset, limit);
        Map<String, Object> userMapping = new HashMap<>();
        userMapping.put("data", users);
        userMapping.put("meta", addPaginateMeta(page, limit));
        return userMapping;
    }

    public User detail(int user_id) {
        return userRepostitory.detail(user_id);
    }

    private Map<String, Object> addPaginateMeta(int page, int limit) {
        Map<String, Object> meta = new HashMap<>();
        Long total = userRepostitory.count();
        meta.put("total", total);
        meta.put("limit", limit);
        meta.put("current_page", page);
        meta.put("last_page", Math.ceil(1.0 * total / limit));
        if (page == 1)
            meta.put("prev_page_url", "");
        else
            meta.put("prev_page_url", "http://localhost:8090/app/users/index?page=" + (page - 1) + "&limit=" + limit);

        if ((page * limit) < total)
            meta.put("next_page_url", "http://localhost:8090/app/users/index?page=" + (page + 1) + "&limit=" + limit);
        else
            meta.put("next_page_url", "");
        meta.put("path", "http://localhost:8090/app/");
        return meta;
    }
}