package com.gm.server.admin.service.impl;

import com.gm.server.common.utils.spring.SpringUtils;
import com.gm.server.admin.domain.GmMail;
import com.gm.server.admin.mapper.GmMailMapper;
import com.gm.server.admin.service.IGmMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/5/10 14:35
 */
@Service
public class GmMailServiceImpl implements IGmMailService {

    @Autowired
    private GmMailMapper gmMailMapper;

    @Override
    public List<GmMail> selectGmMailList(GmMail mail) {
        return gmMailMapper.selectGmMailList(mail);
    }

    @Override
    public List<GmMail> selectMailAll() {
        return SpringUtils.getAopProxy(this).selectGmMailList(new GmMail());
    }

    @Override
    public GmMail selectGmMailById(Long mailId) {
        return gmMailMapper.selectMailById(mailId);
    }

    @Override
    public List<GmMail> selectGmMailByIds(Long[] sids) {
        return gmMailMapper.selectMailByIds(sids);
    }

    @Override
    public int insertGmMail(GmMail mail) {
        return gmMailMapper.insertGmMail(mail);
    }

    @Override
    public int updateGmMail(GmMail mail) {
        return gmMailMapper.updateGmMail(mail);
    }

    @Override
    public int updateGmMailStatus(GmMail mail, Long[] sid) {
        return gmMailMapper.updateGmMailStatus(mail, sid);
    }

    @Override
    public int deleteMailById(Long mailId) {
        return gmMailMapper.deleteGmMailById(mailId);
    }

    @Override
    public int deleteMailByIds(Long[] sids) {
        return gmMailMapper.deleteGmMailByIds(sids);
    }
}
