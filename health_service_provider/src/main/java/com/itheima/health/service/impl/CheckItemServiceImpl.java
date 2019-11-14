package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author zhangmeng
 * @description 检查项service实现类
 * @date 2019/9/16
 **/
@Service
@Slf4j
public class CheckItemServiceImpl implements CheckItemService {
    @Autowired
    private CheckItemDao checkItemDao;
    @Override
    @Transactional
    public void add(CheckItem checkItem) {
        log.info("[检查项-添加]data：{}",checkItem);
        checkItemDao.insert(checkItem);
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        log.info("[检查项-分页查询]data：{}",queryPageBean);
        //使用分页插件
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        // 分页查询数据库
        Page<CheckItem> page = checkItemDao.selectByConditon(queryPageBean.getQueryString());
        //封装分页数据
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    @Transactional
    public void delete(int id) {
        log.info("[检查项-根据id删除]id：{}",id);
        //校验是否可以删除
        Long count = checkItemDao.countCheckGroupByCheckItemId(id);
        if(count >0){
            throw new RuntimeException("当前检查项有数据，不能删除");
        }
        //实际删除操作
        checkItemDao.deleteById(id);
    }

    @Override
    @Transactional
    public void edit(CheckItem checkItem) {
        log.info("[检查项-编辑]data:{}",checkItem);
        checkItemDao.update(checkItem);
    }

    @Override
    public CheckItem findById(Integer id) {
        log.info("[检查项-根据ID查询]id:{}",id);
        return checkItemDao.selectById(id);
    }

    @Override
    public List<CheckItem> findAll() {
        log.info("[检查项-查询所有]~");
        // 调用DAO查询
        return checkItemDao.selectAll();
    }
}
