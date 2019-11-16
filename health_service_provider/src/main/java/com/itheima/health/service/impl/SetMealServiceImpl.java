package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.IOException;
import java.util.ArrayList;
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
    private SetMealDao setmealDao;

    @Transactional
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        log.info("[套餐-添加]data:{},checkgroupIds:{}", setmeal, checkgroupIds);
        // 调用DAO数据入库
        // 插入基本数据
        setmealDao.insert(setmeal);
        //创建集合，
        List<Setmeal> setmealList = new ArrayList<>();
        //查询数据库
        List<Setmeal> setmeals = setmealDao.selectAll();
        //查询数据库后
        for (Setmeal setmeal1 : setmeals) {

            Setmeal setmeal2 = setmealDao.selectById(setmeal.getId());
            setmealList.add(setmeal1);
        }
        // 插入关联数据
        for (Integer checkgroupId : checkgroupIds) {
            setmealDao.insertSetMealAndCheckGroup(setmeal.getId(), checkgroupId);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        //存入缓存
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(RedisConst.SETMEAL_PIC_DB_RESOURCES, setmeal.getImg());
            jedis.del(RedisConst.SETMEAL_DB_RESOURCES);
            for (Setmeal setmeal1 : setmealList) {
                String valueAsString = objectMapper.writeValueAsString(setmeal1);
                jedis.rpush(RedisConst.SETMEAL_DB_RESOURCES,valueAsString);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //设置分页参数
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        //DAO查询数据
        Page<Setmeal> page = setmealDao.selectByCondition(queryPageBean.getQueryString());
        //封装返回值
        return new PageResult(page.getTotal(), page.getResult());
    }



    @Override
    public List<Setmeal> findAll() {
        try (Jedis jedis = jedisPool.getResource()) {
            //创建jackson
            ObjectMapper objectMapper = new ObjectMapper();
            //创建Arraylist集合，
            ArrayList<Setmeal> setmealList = new ArrayList<>();
            //判断，首先去缓存中查看，看看缓存有没有要的数据
            if (jedis.lrange(RedisConst.SETMEAL_DB_RESOURCES,0,-1).size() == 0) {
                //没有，调DAO层，查询数据库
                List<Setmeal> setmealLists = setmealDao.selectAll();
                //循环遍历存入list集合中
                for (Setmeal setmeals : setmealLists) {
                    Setmeal setmeal1 = setmealDao.selectById(setmeals.getId());
                    setmealList.add(setmeal1);
                }
                //json序列化,存入redis中    把对象转换为字节序列的过程称为对象的序列化。
                for (Setmeal setmeals : setmealList) {
                    String setmealCache = objectMapper.writeValueAsString(setmeals);
                    jedis.rpush(RedisConst.SETMEAL_DB_RESOURCES, setmealCache);
                }
                return setmealList;
            } else {
                //创建集合
                ArrayList<Setmeal> setmeal = new ArrayList<>();
                //如果缓存中有要的数据，直接查缓存
                List<String> smembers = jedis.lrange(RedisConst.SETMEAL_DB_RESOURCES,0,-1);
                for (String smember : smembers) {
                    //反序列化：把字节序列恢复为对象的过程称为对象的反序列化
                    Setmeal setmeal2 = objectMapper.readValue(smember, Setmeal.class);
                    setmealList.add(setmeal2);
                }
                return setmealList;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Setmeal findById(Integer id) {
        Setmeal setmealRedis = null;
        //创建jackson
        ObjectMapper objectMapper = new ObjectMapper();
        //去缓存查询
        try (Jedis jedis = jedisPool.getResource()) {
            //查询存入list集合
            List<String> smembers = jedis.lrange(RedisConst.SETMEAL_DB_RESOURCES,0,-1);
            //循环遍历
            for (String smember : smembers) {
                //反序列化：把字节序列恢复为对象的过程称为对象的反序列化
                Setmeal setmeal = objectMapper.readValue(smember, Setmeal.class);
                //根据id传套餐详情，查到了返回给事先定义的setmealRedis，结束
                if (id == setmeal.getId()) {
                    setmealRedis = setmeal;
                    break;
                }
            }
            //缓存没查到，为null，直接查数据库 ，
            if (setmealRedis == null) {
                setmealRedis = setmealDao.selectById(id);
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //调用DAO查询数据
        return setmealRedis;
    }


}
