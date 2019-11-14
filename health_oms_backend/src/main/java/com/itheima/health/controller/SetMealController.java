package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.common.MessageConst;
import com.itheima.health.common.RedisConst;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetMealService;
import com.itheima.health.utils.QiniuUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.UUID;

/**
 * @author zhangmeng
 * @description 套餐控制器
 * @date 2019/9/26
 **/
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private JedisPool jedisPool ;

    @Reference
    private SetMealService setMealService;
    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile multipartFile) {
        log.info("文件上传，name:{},size:{}", multipartFile.getOriginalFilename(), multipartFile.getSize());
        //原始文件名
        String originalFileName = multipartFile.getOriginalFilename();
        //使用UUID构造不重复的文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + originalFileName;

        //获取输入流并上传
        try (InputStream is = multipartFile.getInputStream()) {
            QiniuUtils.upload2Qiniu(is, fileName);
        } catch (RuntimeException|IOException e) {
            log.error("", e);
            return new Result(false, MessageConst.PIC_UPLOAD_FAIL);
        }
        try(Jedis jedis = jedisPool.getResource()){
            jedis.sadd(RedisConst.SETMEAL_PIC_RESOURCES,fileName);
        }
        //构造返回值
        String pic = QiniuUtils.QINIU_IMG_URL_PRE + fileName;
        return new Result(true, MessageConst.PIC_UPLOAD_SUCCESS, pic);
    }

    /**
     * t添加套餐
     * @param setmeal   套餐基本信息
     * @param checkgroupIds 检查组ID列表
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Setmeal setmeal , Integer[] checkgroupIds) {
        log.info("[套餐-添加]data:{},checkgroupIds:{}",setmeal,checkgroupIds);
        if(!StringUtils.isEmpty(setmeal.getImg())) {
            String img =setmeal.getImg().replace(QiniuUtils.QINIU_IMG_URL_PRE,"");
            setmeal.setImg(img);
        }
        try {
            //RPC调用添加数据
            setMealService.add(setmeal,checkgroupIds);
            return new Result(true,MessageConst.ADD_SETMEAL_SUCCESS);
        } catch (RuntimeException e) {
            log.error("",e);
            return new Result(false,MessageConst.ADD_SETMEAL_FAIL+":"+e.getMessage());
        }
    }

    /**
     * 分页查询
     * @return
     */
    @GetMapping("/findPage")
    public PageResult findPage(QueryPageBean queryPageBean){
        try {
            //调用DAO层查询并返回
            return setMealService.findPage(queryPageBean);
        } catch (RuntimeException e) {
            log.error("",e);
            return new PageResult(0L, Collections.emptyList());
        }

    }
}
