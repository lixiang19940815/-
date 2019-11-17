package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.JobDao;
import com.itheima.health.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service(interfaceClass = JobService.class)
public class JobServiceImpl implements JobService {

    @Autowired
    private JobDao jobDao;
    @Override
    public void delete() {
        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        jobDao.delete(nowDate);
    }
}
