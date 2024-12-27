package com.ycw.gm.admin.service.impl;

import com.ycw.gm.admin.domain.RechargeDetail;
import com.ycw.gm.admin.mapper.RechargeDetailMapper;
import com.ycw.gm.admin.service.IRechargeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wishcher tree
 * @date 2022/8/3 16:20
 */
@Service
public class RechargeDetailServiceImpl implements IRechargeDetailService {
    @Autowired
    private RechargeDetailMapper rechargeDetailMapper;

//    public int countRechargeNum(String dbName) {
//
//        List<String> strings = rechargeDetailMapper.countTable(dbName);
//        AtomicLong count = new AtomicLong();
//        for (String tabName : strings) {
//            if (!tabName.startsWith("@")) {
//                continue;
//            }
//            long countNum = rechargeDetailMapper.countRecharge(tabName);
//            count.addAndGet(countNum);
//        }
//        return (int) count.get();
//    }

    public Long sumRecharge() {
        return rechargeDetailMapper.sumRecharge();
//        List<String> strings = rechargeDetailMapper.countTable(dbName);
//        long sum = 0;
//        for (String tabName : strings) {
//            Long l = rechargeDetailMapper.sumRecharge(tabName);
//            if (l != null) {
//                sum += l;
//
//            }
//        }
//        return sum;
    }

    public List<RechargeDetail> selectAllRecharge(RechargeDetail detail) {
        return rechargeDetailMapper.selectAllRecharge(detail);
//        PageDomain pageDomain = TableSupport.buildPageRequest();
//        int pageNum = pageDomain.getPageNum() - 1;
//        int pageSize = pageDomain.getPageSize();
//
//        int startSize = pageSize * pageNum;
//
//        List<RechargeDetail> logList = new ArrayList<>();
//
//        List<String> strings = rechargeDetailMapper.countTable(dbName);
//        List<String> tabsSort = strings.stream().sorted((a,b) -> String.CASE_INSENSITIVE_ORDER.compare(b, a)).collect(Collectors.toList());
//        int sumCount = 0;
//        for (String tabName : tabsSort) {
//            if (!tabName.startsWith("@")) {
//                continue;
//            }
//
//            try {
//                long countNum = rechargeDetailMapper.countRecharge(tabName);
//                if (sumCount + countNum > startSize) {
//                    long limitStart = 0;
//                    if (sumCount >= startSize) {
//                    } else {
//                        limitStart = startSize - sumCount;
//                    }
//                    long limitEnd = Math.min(pageSize - logList.size(), countNum);
//                    List<RechargeDetail> logs = rechargeDetailMapper.selectAllRecharge(tabName, limitStart, limitEnd);
//                    logList.addAll(logs);
//                    if (logList.size() >= pageSize) {
//                        break;
//                    }
//                }
//                sumCount += countNum;
//            } catch (Exception e) {
//                throw e;
//            }
//
//        }
//        return logList;
    }

}
