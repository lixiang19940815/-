package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.CheckItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangmeng
 * @description j检查组dao层
 * @date 2019/9/16
 **/
public interface CheckItemDao {
    /**
     * 插入
     * @param checkItem
     */
    void insert(CheckItem  checkItem);

    /**
     * 分页查询
     * @param queryString
     * @return
     */
    Page<CheckItem> selectByConditon(@Param("queryString") String queryString);

    /**
     * 根据ID删除
     * @param id
     */
    void deleteById(@Param("id") Integer id);

    /**
     * 根据检查项id统计关联的检查组数量
     * @param checkItemId
     * @return
     */
    Long countCheckGroupByCheckItemId(@Param("checkItemId") Integer checkItemId);

    /**
     * 更新
     * @param checkItem
     */
    void update(CheckItem checkItem);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    CheckItem selectById(@Param("id") Integer id);

    /**
     * 查询所有检查项
     * @return
     */
    List<CheckItem> selectAll();
}
