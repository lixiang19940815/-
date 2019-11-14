package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.common.RedisConst;
import com.itheima.health.dao.SetMealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @author zhangmeng
 * @description 套餐SEVICE实现类
 * @date 2019/9/26
 **/
@Service(interfaceClass = SetMealService.class)
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private SetMealDao setMealDao;
    @Transactional
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        log.info("[套餐-添加]data:{},checkgroupIds:{}",setmeal,checkgroupIds);
        // 调用DAO数据入库
        // 插入基本数据
        setMealDao.insert(setmeal);
        // 插入关联数据
        for (Integer checkgroupId : checkgroupIds) {
            setMealDao.insertSetMealAndCheckGroup(setmeal.getId(),checkgroupId);
        }
        try(Jedis jedis = jedisPool.getResource()){
            jedis.sadd(RedisConst.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
        }
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //设置分页参数
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //DAO查询数据
        Page<Setmeal> page = setMealDao.selectByCondition(queryPageBean.getQueryString());
        //封装返回值
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Setmeal> findAll() {
        //调用DAO查询所有
        return setMealDao.selectAll();
    }

    @Override
    public Setmeal findById(Integer id) {
        //调用DAO查询数据
        return setMealDao.selectById(id);
    }
}
