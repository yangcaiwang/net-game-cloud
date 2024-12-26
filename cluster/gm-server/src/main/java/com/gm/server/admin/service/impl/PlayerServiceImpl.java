package com.gm.server.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gm.server.admin.domain.*;
import com.gm.server.admin.domain.vo.GeneralVo;
import com.gm.server.admin.domain.vo.ItemVo;
import com.gm.server.admin.mapper.*;
import com.gm.server.admin.service.IPlayerService;
import com.gm.server.common.core.page.PageDomain;
import com.gm.server.common.core.page.TableSupport;
import com.gm.server.common.utils.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 玩家 业务层处理
 *
 * @author gamer
 */
@Service
public class PlayerServiceImpl implements IPlayerService {

    private static final Logger log = Logger.getLogger("PlayerServiceImpl");
    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private GeneralMapper generalMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemLogMapper itemLogMapper;

    @Autowired
    private PlayerMailMapper playerMailMapper;

    @Autowired
    private PowerLogMapper powerLogMapper;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private GeneralLogMapper generalLogMapper;

    /**
     * 根据条件分页查询玩家数据
     *
     * @param player 玩家信息
     * @return 玩家数据集合信息
     */
    @Override
    public List<Player> selectPlayerList(Player player) {
        return playerMapper.selectPlayerList(player);
    }

    /**
     * 查询所有玩家
     *
     * @return 玩家列表
     */
    @Override
    public List<Player> selectPlayerAll() {
        return SpringUtils.getAopProxy(this).selectPlayerList(new Player());
    }

    /**
     * 通过玩家ID查询玩家
     *
     * @param playerId 玩家ID
     * @return 玩家对象信息
     */
    @Override
    public Player selectPlayerById(Long playerId) {
        return playerMapper.selectPlayerById(playerId);
    }

    @Override
    public PlayerBase selectPlayerBaseById(Long playerId) {
        return playerMapper.selectPlayerBaseById(playerId);
    }

//    @Override
//    public List<General> selectGeneralById(Long roleId) {
//        return generalMapper.selectGeneralById(roleId);
//    }

    @Override
    public List<ItemVo> selectItemById(Long roleId) {
        Item item = itemMapper.selectItemById(roleId);
        if (item != null) {
            PageDomain pageDomain = TableSupport.buildPageRequest();
            int pageNum = pageDomain.getPageNum() - 1;
            int pageSize = pageDomain.getPageSize();
            String itemStr = item.getItemData();
            List<ItemVo> list = new ArrayList<>();
            Map resultMap = JSON.parseObject(itemStr, Map.class);
            resultMap.values().stream().sorted((a, b) -> {
                JSONObject objectA = (JSONObject) a;
                JSONObject objectB = (JSONObject) b;
                return Integer.compare(objectA.getInteger("index"), objectB.getInteger("index"));
            }).skip((long) pageNum * pageSize).limit(pageSize).forEach(v -> {
                JSONObject object = (JSONObject) v;
                ItemVo itemVo = new ItemVo();
                itemVo.toItemVo(object);
                list.add(itemVo);
            });

            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public List<GeneralVo> selectGeneralById(Long roleId, boolean all) {
        General general = generalMapper.selectGeneralById(roleId);
        if (general != null) {

            String generalMap = general.getGeneralMap();
            Map resultMap = JSON.parseObject(generalMap, Map.class);
            List<GeneralVo> list = new ArrayList<>();

            if (all) {
                resultMap.values().stream().sorted((a, b) -> {
                    JSONObject objectA = (JSONObject) a;
                    JSONObject objectB = (JSONObject) b;
                    return Integer.compare(objectA.getInteger("generalId"), objectB.getInteger("generalId"));
                }).forEach(v -> {
                    JSONObject object = (JSONObject) v;
                    GeneralVo generalVo = new GeneralVo();
                    generalVo.toGeneralVo(object);
                    list.add(generalVo);
                });
                return list;
            }

            PageDomain pageDomain = TableSupport.buildPageRequest();
            int pageNum = pageDomain.getPageNum() - 1;
            int pageSize = pageDomain.getPageSize();

//            String generalMap = general.getGeneralMap();
//            Map resultMap = JSON.parseObject(generalMap, Map.class);
            resultMap.values().stream().sorted((a, b) -> {
                JSONObject objectA = (JSONObject) a;
                JSONObject objectB = (JSONObject) b;
                return Integer.compare(objectA.getInteger("generalId"), objectB.getInteger("generalId"));
            }).skip((long) pageNum * pageSize).limit(pageSize).forEach(v -> {
                JSONObject object = (JSONObject) v;
                GeneralVo generalVo = new GeneralVo();
                generalVo.toGeneralVo(object);
                list.add(generalVo);
            });

            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public int countGeneralVo(Long roleId) {
        General general = generalMapper.selectGeneralById(roleId);
        if (general != null) {
            Map resultMap = JSON.parseObject(general.getGeneralMap(), Map.class);
            return resultMap.size();
        }
        return 0;
    }

    @Override
    public List<ItemVo> selectItemByIdAll(Long roleId) {
        Item item = itemMapper.selectItemById(roleId);
        if (item != null) {
            String itemStr = item.getItemData();
            List<ItemVo> list = new ArrayList<>();
            Map resultMap = JSON.parseObject(itemStr, Map.class);
            resultMap.values().forEach(v -> {
                JSONObject object = (JSONObject) v;
                ItemVo itemVo = new ItemVo();
                itemVo.toItemVo(object);
                list.add(itemVo);
            });

            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public int countItem(Long roleId) {
        Item item = itemMapper.selectItemById(roleId);
        if (item != null) {
            Map resultMap = JSON.parseObject(item.getItemData(), Map.class);
            return resultMap.size();
        }
        return 0;
    }

    @Override
    public int countItemLogNum(String dbName, Long roleId, ItemLog itemLog) {

        List<String> strings = itemLogMapper.countTable(dbName);
        List<String> tabsSort = strings.stream().sorted((a,b) -> String.CASE_INSENSITIVE_ORDER.compare(b, a)).collect(Collectors.toList());
        AtomicLong count = new AtomicLong();
        for (String tabName : tabsSort) {
            if (!tabName.startsWith("@")) {
                continue;
            }
            long countNum = itemLogMapper.countItemLog(tabName, roleId, itemLog);
            count.addAndGet(countNum);
        }
        return (int) count.get();
    }

    @Override
    public int countPowerLogNum(String dbName, Long roleId, PowerLog powerLog) {

        List<String> strings = powerLogMapper.countTable(dbName);
        List<String> tabsSort = strings.stream().sorted((a,b) -> String.CASE_INSENSITIVE_ORDER.compare(b, a)).collect(Collectors.toList());
        AtomicLong count = new AtomicLong();
        for (String tabName : tabsSort) {
            if (!tabName.startsWith("@")) {
                continue;
            }
            long countNum = powerLogMapper.countPowerLog(tabName, roleId, powerLog);
            count.addAndGet(countNum);
        }
        return (int) count.get();
    }

    @Override
    public int countLoginLogNum(String dbName, LoginLog log) {
        List<String> strings = loginLogMapper.countTable(dbName);
        List<String> tabsSort = strings.stream().sorted((a,b) -> String.CASE_INSENSITIVE_ORDER.compare(b, a)).collect(Collectors.toList());
        AtomicLong count = new AtomicLong();
        for (String tabName : tabsSort) {
            if (!tabName.startsWith("@")) {
                continue;
            }
            long countNum = loginLogMapper.countLog(tabName, log);
            count.addAndGet(countNum);
        }
        return (int) count.get();
    }

    @Override
    public int countGeneralLogNum(String dbName, GeneralLog log) {
        List<String> strings = generalLogMapper.countTable(dbName);
        List<String> tabsSort = strings.stream().sorted((a,b) -> String.CASE_INSENSITIVE_ORDER.compare(b, a)).collect(Collectors.toList());
        AtomicLong count = new AtomicLong();
        for (String tabName : tabsSort) {
            if (!tabName.startsWith("@")) {
                continue;
            }
            long countNum = generalLogMapper.countLog(tabName, log);
            count.addAndGet(countNum);
        }
        return (int) count.get();
    }

    @Override
    public List<ItemLog> selectItemLogById(String dbName, Long roleId, ItemLog itemLog, boolean all) {

        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() - 1;
        int pageSize = pageDomain.getPageSize();

        int startSize = pageSize * pageNum;
//        int endSize = pageSize;

        List<ItemLog> logList = new ArrayList<>();

        List<String> strings = itemLogMapper.countTable(dbName);
        List<String> tabsSort = strings.stream().sorted((a,b) -> String.CASE_INSENSITIVE_ORDER.compare(b, a)).collect(Collectors.toList());
        int sumCount = 0;
        for (String tabName : tabsSort) {
            if (!tabName.startsWith("@")) {
                continue;
            }

            if (all) {
                List<ItemLog> logs = itemLogMapper.selectItemLogByIdAll(tabName, roleId, itemLog);
                logList.addAll(logs);
                continue;
            }

            try {
                long countNum = itemLogMapper.countItemLog(tabName, roleId, itemLog);
                if (sumCount + countNum > startSize) {
                    long limitStart = 0;
                    if (sumCount >= startSize) {
                    } else {
                        limitStart = startSize - sumCount;
                    }
                    long limitEnd = Math.min(pageSize - logList.size(), countNum);
                    List<ItemLog> logs = itemLogMapper.selectItemLogById(tabName, roleId, itemLog, limitStart, limitEnd);
                    logList.addAll(logs);
                    if (logList.size() >= pageSize) {
                        break;
                    }
                }
                sumCount += countNum;
            } catch (Exception e) {
                throw e;
            }

        }
        return logList;
    }

    @Override
    public List<PowerLog> selectPowerLogById(String dbName, Long roleId, PowerLog powerLog, boolean all) {

        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() - 1;
        int pageSize = pageDomain.getPageSize();

        int startSize = pageSize * pageNum;
//        int endSize = pageSize;

        List<PowerLog> logList = new ArrayList<>();

        List<String> strings = powerLogMapper.countTable(dbName);
        List<String> tabsSort = strings.stream().sorted((a,b) -> String.CASE_INSENSITIVE_ORDER.compare(b, a)).collect(Collectors.toList());
        int sumCount = 0;
        for (String tabName : tabsSort) {
            if (!tabName.startsWith("@")) {
                continue;
            }

            if (all) {
                List<PowerLog> logs = powerLogMapper.selectPowerLogByIdAll(tabName, roleId, powerLog);
                logList.addAll(logs);
                continue;
            }

            try {
                long countNum = powerLogMapper.countPowerLog(tabName, roleId, powerLog);
                if (sumCount + countNum > startSize) {
                    long limitStart = 0;
                    if (sumCount >= startSize) {
                    } else {
                        limitStart = startSize - sumCount;
                    }
                    long limitEnd = Math.min(pageSize - logList.size(), countNum);
                    List<PowerLog> logs = powerLogMapper.selectPowerLogById(tabName, roleId, powerLog, limitStart, limitEnd);
                    logList.addAll(logs);
                    if (logList.size() >= pageSize) {
                        break;
                    }
                }
                sumCount += countNum;
            } catch (Exception e) {
                throw e;
            }

        }
        return logList;
    }

    @Override
    public List<LoginLog> selectLoginLogById(String dbName, LoginLog log, boolean all) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() - 1;
        int pageSize = pageDomain.getPageSize();

        int startSize = pageSize * pageNum;

        List<LoginLog> logList = new ArrayList<>();

        List<String> strings = loginLogMapper.countTable(dbName);
        List<String> tabsSort = strings.stream().sorted((a,b) -> String.CASE_INSENSITIVE_ORDER.compare(b, a)).collect(Collectors.toList());
        int sumCount = 0;
        for (String tabName : tabsSort) {
            if (!tabName.startsWith("@")) {
                continue;
            }

            if (all) {
                List<LoginLog> logs = loginLogMapper.selectLoginLogByIdAll(tabName, log);
                logList.addAll(logs);
                continue;
            }

            try {
                long countNum = loginLogMapper.countLog(tabName, log);
                if (sumCount + countNum > startSize) {
                    long limitStart = 0;
                    if (sumCount >= startSize) {
                    } else {
                        limitStart = startSize - sumCount;
                    }
                    long limitEnd = Math.min(pageSize - logList.size(), countNum);
                    List<LoginLog> logs = loginLogMapper.selectLoginLogById(tabName, log, limitStart, limitEnd);
                    logList.addAll(logs);
                    if (logList.size() >= pageSize) {
                        break;
                    }
                }
                sumCount += countNum;
            } catch (Exception e) {
                throw e;
            }

        }
        return logList;
    }

    @Override
    public List<GeneralLog> selectGeneralLogById(String dbName, GeneralLog log, boolean all) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() - 1;
        int pageSize = pageDomain.getPageSize();

        int startSize = pageSize * pageNum;

        List<GeneralLog> logList = new ArrayList<>();

        List<String> strings = generalLogMapper.countTable(dbName);
        List<String> tabsSort = strings.stream().sorted((a,b) -> String.CASE_INSENSITIVE_ORDER.compare(b, a)).collect(Collectors.toList());
        int sumCount = 0;
        for (String tabName : tabsSort) {
            if (!tabName.startsWith("@")) {
                continue;
            }

            if (all) {
                List<GeneralLog> logs = generalLogMapper.selectGeneralLogByIdAll(tabName, log);
                logList.addAll(logs);
                continue;
            }

            try {
                long countNum = generalLogMapper.countLog(tabName, log);
                if (sumCount + countNum > startSize) {
                    long limitStart = 0;
                    if (sumCount >= startSize) {
                    } else {
                        limitStart = startSize - sumCount;
                    }
                    long limitEnd = Math.min(pageSize - logList.size(), countNum);
                    List<GeneralLog> logs = generalLogMapper.selectGeneralLogById(tabName, log, limitStart, limitEnd);
                    logList.addAll(logs);
                    if (logList.size() >= pageSize) {
                        break;
                    }
                }
                sumCount += countNum;
            } catch (Exception e) {
                throw e;
            }

        }
        return logList;
    }

    @Override
    public List<PlayerMail> selectPlayerMailById(Long playerId, PlayerMail playerMail) {
        return playerMailMapper.selectPlayerMailById(playerId, playerMail);
    }
}
