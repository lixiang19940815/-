package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhangmeng
 * @description
 * @date 2019/9/18
 **/
@Service
@Slf4j
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;
    @Transactional
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        log.info("[检查组-添加]data:{},checkitemIds：{}", checkGroup, checkitemIds);
        // 插入基本数据
        checkGroupDao.insert(checkGroup);
        //逐条插入关系表数据
        for (Integer checkItemId : checkitemIds) {
            checkGroupDao.insertCheckGroupAndCheckItem(checkGroup.getId(),checkItemId);
        }
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        log.info("[检查组-分页查询]data：{}",queryPageBean);
        //设置分页参数
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //查询page
        Page<CheckGroup>  page= checkGroupDao.selectByCondition(queryPageBean.getQueryString());
        //封装返回值
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.selectById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.selectCheckItemIdsByCheckGroupId(id);
    }

    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1、更新检查组
        checkGroupDao.update(checkGroup);
        //2、更新关联关系
        //2.1、删除原有关系
        checkGroupDao.deleteCheckGroupAndCheckItemByCheckGroupId(checkGroup.getId());
        //2.2、加入当前关联关系
        for (Integer checkitemId : checkitemIds) {
            checkGroupDao.insertCheckGroupAndCheckItem(checkGroup.getId(),checkitemId);
        }


    }

    @Override
    public List<CheckGroup> findAll() {
        //调用DAO查询数据并返回
        return checkGroupDao.selectAll();
    }
}
