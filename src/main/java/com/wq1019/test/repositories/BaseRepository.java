package com.wq1019.test.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.PageHelper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

public class BaseRepository<T> {

    @Autowired
    private Mapper<T> mapper = null;

    public T findOne(long id) {

        return mapper.selectByPrimaryKey(id);
    }

    public List<T> findAll() {
        return mapper.selectAll();
    }

    public int save(T e) {
        return mapper.insert(e);
    }

    public void update(T e) {
        mapper.updateByPrimaryKey(e);
    }

    public void deleteById(Long e) {
        mapper.deleteByPrimaryKey(e);
    }

    public List<T> queryByPage(Example e, int skip, int limit) {
        PageHelper.offsetPage(skip, limit);
        return mapper.selectByExample(e);
    }

    public int count(T t) {
        return mapper.selectCount(t);
    }

}
