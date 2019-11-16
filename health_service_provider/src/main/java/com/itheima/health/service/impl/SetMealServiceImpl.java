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
        List<Setmeal> setmealList = new ArrayList<>();
        List<Setmeal> setmeals = setmealDao.selectAll();
        for (Setmeal setmeal1 : setmeals) {
            Setmeal setmeal2 = setmealDao.selectById(setmeal.getId());
            setmealList.add(setmeal1);
        }
        // 插入关联数据
        for (Integer checkgroupId : checkgroupIds) {
            setmealDao.insertSetMealAndCheckGroup(setmeal.getId(), checkgroupId);
        }
        ObjectMapper objectMapper = new ObjectMapper();

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
            //数据库
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<Setmeal> setmealList2 = new ArrayList<>();
            if (jedis.lrange(RedisConst.SETMEAL_DB_RESOURCES,0,-1).size() == 0) {
                List<Setmeal> setmealList = setmealDao.selectAll();
                for (Setmeal setmeal : setmealList) {
                    Setmeal setmeal1 = setmealDao.selectById(setmeal.getId());
                    setmealList2.add(setmeal1);
                }
                //序列化
                for (Setmeal setmeal : setmealList2) {
                    String setmealCache = objectMapper.writeValueAsString(setmeal);
                    jedis.rpush(RedisConst.SETMEAL_DB_RESOURCES, setmealCache);
                }
                return setmealList;
            } else {
                ArrayList<Setmeal> setmealList = new ArrayList<>();
                //缓存
                List<String> smembers = jedis.lrange(RedisConst.SETMEAL_DB_RESOURCES,0,-1);
                for (String smember : smembers) {
                    Setmeal setmeal = objectMapper.readValue(smember, Setmeal.class);
                    setmealList.add(setmeal);
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
        Setmeal setmealResult = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> smembers = jedis.lrange(RedisConst.SETMEAL_DB_RESOURCES,0,-1);
            for (String smember : smembers) {
                Setmeal setmeal = objectMapper.readValue(smember, Setmeal.class);
                if (id == setmeal.getId()) {
                    setmealResult = setmeal;
                    break;
                }
            }
            if (setmealResult == null) {
                setmealResult = setmealDao.selectById(id);
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //调用DAO查询数据
        return setmealResult;
    }


}
