package com.wq1019.test.user.repositories;

import com.wq1019.test.user.model.User;
import com.wq1019.test.util.SLUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class UserRepostitory {

    public int store(User user) {
        String sql = "insert into users values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        QueryRunner runner = new QueryRunner(SLUtils.getDataSource());
        int affectedRows = 0;
        try {
            affectedRows = runner.update(sql, user.getId(), user.getName(), user.getAddress(),
                    user.getSex(), user.getEmail(), user.getBirthday(), user.getPostcode(),
                    user.getDescription(), user.getCreated_at(), user.getUpdated_at(),
                    user.getProvince(), user.getProvince_id(), user.getCity(), user.getCity_id(),
                    user.getArea(), user.getArea_id()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectedRows;
    }

    public int save(int user_id, User user) {
        String sql = "update users " +
                "set name=?,sex=?,address=?,email=?,birthday=?,postcode=?,description=?,updated_at=?," +
                "province=?,province_id=?,city=?,city_id=?,area=?,area_id=? " +
                "where id=" + user_id;
        QueryRunner runner = new QueryRunner(SLUtils.getDataSource());
        int affectedRows = 0;
        try {
            affectedRows = runner.update(sql, user.getName(), user.getSex(), user.getAddress(), user.getEmail(),
                    user.getBirthday(), user.getPostcode(), user.getDescription(), user.getUpdated_at(),
                    user.getProvince(), user.getProvince_id(), user.getCity(), user.getCity_id(),
                    user.getArea(), user.getArea_id()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectedRows;
    }

    public int delete(int id) {
        String sql = "delete from users where id=?";
        QueryRunner runner = new QueryRunner(SLUtils.getDataSource());
        int affectedRows = 0;
        try {
            affectedRows = runner.update(sql, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectedRows;
    }

    public List<User> query(int offset, int limit) {
        List<User> users = null;
        String sql = "select * from users order by created_at desc, updated_at desc limit ?,?";
        QueryRunner runner = new QueryRunner(SLUtils.getDataSource());
        try {
            users = runner.query(sql, new BeanListHandler<User>(User.class), offset, limit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public User detail(int user_id) {
        String sql = "select * from users where id=?";
        User user = null;
        QueryRunner runner = new QueryRunner(SLUtils.getDataSource());
        try {
            user = runner.query(sql, new BeanHandler<User>(User.class), user_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Long count() {
        String sql = "select count(*) from users";
        QueryRunner runner = new QueryRunner(SLUtils.getDataSource());
        Long num;
        try {
            num = runner.query(sql, new ScalarHandler<Long>());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("count查询失败" + e);
        }
        return num;
    }
}
