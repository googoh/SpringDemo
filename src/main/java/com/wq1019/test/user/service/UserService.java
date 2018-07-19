package com.wq1019.test.user.service;

import com.wq1019.test.user.repositories.UserRepostitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wq1019.test.user.model.User;
import tk.mybatis.mapper.entity.Example;

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

    public void store(User user) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setCreated_at(timestamp.toString());
        user.setUpdated_at(timestamp.toString());
        userRepostitory.save(user);
    }

    public void delete(Long id) {
        userRepostitory.deleteById(id);
    }

    public Map<String, Object> query(int page, int limit) {
        page = (page < 1) ? 1 : page;
        int offset = (page - 1) * limit;
        // 排序輸出
        Example e = new Example(User.class);
        e.orderBy("updated_at").desc();
        List<User> users = userRepostitory.queryByPage(e, offset, limit);
        // 組裝數據
        Map<String, Object> userMapping = new HashMap<>();
        userMapping.put("data", users);// 組裝 data
        userMapping.put("meta", addPaginateMeta(page, limit));// 組裝 meta
        return userMapping;
    }

    /**
     * 顯示某一條記錄的詳情
     *
     * @param user_id
     * @return
     */
    public User detail(int user_id) {
        return userRepostitory.findOne(user_id);
    }

    /**
     * 添加
     * @param page
     * @param limit
     * @return
     */
    private Map<String, Object> addPaginateMeta(int page, int limit) {
        Map<String, Object> meta = new HashMap<>();
        int total = userRepostitory.count(null);
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