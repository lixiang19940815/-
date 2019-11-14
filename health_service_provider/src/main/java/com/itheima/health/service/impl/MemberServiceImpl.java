package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.exception.DuplicateOperateException;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员SERVICE实现类
 */
@Service(interfaceClass = MemberService.class)
@Slf4j
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;

    @Transactional
    @Override
    public Member addMember(Member member) throws DuplicateOperateException {
        log.info("[会员-添加]data:{}", member);
        if (null != this.findByPhone(member.getPhoneNumber())) {
            //会员已添加，重复操作
            throw new DuplicateOperateException("会员已存在");
        }
        //调用DAO插入数据
        memberDao.insert(member);
        return memberDao.selectById(member.getId());
    }

    @Override
    public Member findByPhone(String phoneNumber) {
        return memberDao.selectPhoneNumber(phoneNumber);
    }

    @Override
    public Member findById(Integer id) {

        return memberDao.selectById(id);
    }

    @Override
    public List<Integer> countByMonth(List<String> months) {
        List<Integer> resultLIst = new ArrayList<>();
        for (String month : months) {
            String endDate = month+".31";
            Long count = memberDao.countByRegTimeBefore(endDate);
            resultLIst.add(count.intValue());
        }
        return resultLIst;
    }
}
