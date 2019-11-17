package com.itheima.health.jobs;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.service.JobService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ClearOrderSetting {
    /**
     * 清理预约设置
     */
    @Reference
    private JobService jobService;

    public void clearOrdersetting() {
        log.info("清理预约设置历史数据......✈✈✈✈✈✈✈");
        try {
            jobService.delete();
            log.info("您已成功清理掉ordersetting表中的垃圾数据！！！");
        } catch (RuntimeException e) {
            log.error("", e);
            log.info("很遗憾，您失败了！！");
        }
    }
}
