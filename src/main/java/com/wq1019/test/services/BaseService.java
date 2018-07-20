package com.wq1019.test.services;

import com.wq1019.test.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseService<T> {

    @Autowired
    private BaseRepository<T> baseRepository;

    /**
     * 添加一條數據
     *
     * @param t
     */
    public void store(T t) {
        baseRepository.save(t);
    }

    /**
     * 顯示某一條記錄的詳情
     *
     * @param id
     * @return
     */
    public T findById(Long id) {
        return baseRepository.findOne(id);
    }

    /**
     * 刪除一條記錄
     *
     * @param id
     */
    public void delete(Long id) {
        baseRepository.deleteById(id);
    }

    public List<T> pagination(Example e, int offset, int limit) {
        return baseRepository.queryByPage(e, offset, limit);
    }

    /**
     * 添加分頁 Meta 方便前端使用
     *
     * @param page
     * @param limit
     * @return
     */
    public Map<String, Object> addPaginateMeta(int page, int limit) {
        Map<String, Object> meta = new HashMap<>();
        int total = baseRepository.count(null);
        meta.put("total", total);
        meta.put("limit", limit);
        meta.put("current_page", page);
        meta.put("last_page", Math.ceil(1.0 * total / limit));
        if (page == 1)
            meta.put("prev_page_url", "");
        else
            meta.put("prev_page_url", "/app/users/index?page=" + (page - 1) + "&limit=" + limit);

        if ((page * limit) < total)
            meta.put("next_page_url", "/app/users/index?page=" + (page + 1) + "&limit=" + limit);
        else
            meta.put("next_page_url", "");
        meta.put("path", "http://localhost:8090/app/");

        return meta;
    }
}
