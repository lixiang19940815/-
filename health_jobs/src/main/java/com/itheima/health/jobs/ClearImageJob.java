package com.itheima.health.jobs;

import com.itheima.health.common.RedisConst;
import com.itheima.health.utils.QiniuUtils;
import com.qiniu.common.QiniuException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * @author zhangmeng
 * @description 定时任务：清理垃圾图片
 * @date 2019/9/26
 **/
@Slf4j
public class ClearImageJob {
    @Autowired
    private JedisPool jedisPool;
    /**
     * 定义清理图片的任务
     */
    public void clearImageJob(){
        log.info("[清理垃圾图片]开始……");
        //清理垃圾图片
        try(Jedis jedis = jedisPool.getResource()){
            //计算redis中两个集合的差值，获取垃圾图片名称
            Set<String> set = jedis.sdiff(RedisConst.SETMEAL_PIC_RESOURCES, RedisConst.SETMEAL_PIC_DB_RESOURCES);
            for (String img : set) {
                log.info("[清理垃圾图片]target:{}",img);

                //从七牛云移除
                QiniuUtils.deleteFileFromQiniu(img);
                //从redis移除
                jedis.srem(RedisConst.SETMEAL_PIC_RESOURCES,img);
            }
        } catch (QiniuException e) {
            log.error("[清理垃圾图片]异常",e);
        } finally {
            log.info("[清理垃圾图片]完成……");
        }

    }
}
