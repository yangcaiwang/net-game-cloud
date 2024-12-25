package com.common.module.internal.ranklist;

import com.common.module.util.CollectionUtils;
import com.common.module.util.StringUtils;
import com.common.module.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.common.module.internal.thread.task.linked.AbstractLinkedTask;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
/**
 * <跨服排行榜实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class CrossRankLists implements ICrossRankLists {
    private static final Logger log = LoggerFactory.getLogger(RankLists.class);

    private final Map<String, ICrossRankList<? extends AbstractRankMember<?>>> rankLists;

    /**
     * 把排行榜创建序号拼接到名称后面,用来做排行榜重置功能,因为修改了名称所以异步旧任务提交的成绩会被忽略
     */
    private final Map<String, AtomicLong> rankIndexNames;

    private final Executor executor;

    private CrossRankLists() {
        rankLists = Maps.newConcurrentMap();
        rankIndexNames = Maps.newConcurrentMap();
        executor = new ActorThreadPoolExecutor(null, "ranklist-sort", Runtime.getRuntime().availableProcessors() / 2 + 1, 5, 200, 0);
    }
    @Override
    public <E extends AbstractRankMember<E>> void initMembers(String name, int groupId, Iterable<E> members) {
        if (CollectionUtils.isEmpty(members))
            return;

        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return;
        }
        iRankList.initMembers(groupId, members);
    }

    private String rankName(String name) {

        if (!rankIndexNames.containsKey(name)) {
            return name;
        }
        return StringUtils.merged(name, rankIndexNames.get(name).get());
    }

    @Override
    public void createRankList(String name, int total) {
        if (rankLists.keySet().stream().anyMatch(n -> n.contains(name)))
            throw new RuntimeException("排行榜名称重复:name=" + name);
        if (total < 2)
            throw new RuntimeException("排行榜容量非法:name=" + name + ",total=" + total);
        rankIndexNames.computeIfAbsent(name, v -> new AtomicLong()).incrementAndGet();
        String rankName = rankName(name);
        rankLists.computeIfAbsent(rankName, v -> new CrossRankList<>(name, total));
        log.warn("创建排行榜:" + rankName + "," + total);
    }

    @Override
    public void resetRankList(String name) {
        String rankName = rankName(name);
        ICrossRankList<? extends AbstractRankMember<?>> iRankList = rankLists.remove(rankName);
        log.warn("删除排行榜:" + rankName + "," + iRankList);
        createRankList(name, iRankList.getTotal());
    }

    @Override
    public <E extends AbstractRankMember<E>> void commit(String name, int groupId, E member) {
        String rankName = rankName(name);
        executor.execute(new AbstractLinkedTask() {
            @Override
            public int getSlowly() {
                return 10;
            }

            @Override
            public Object getIdentity() {
                return rankName;
            }

            @Override
            protected void exec() throws Exception {
                ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(rankName);
                if (iRankList == null) {
                    return;
                }
                iRankList.commit(groupId, member);
            }
        });
    }

    @Override
    public <E extends AbstractRankMember<E>> void commitIfNotExist(String name, int groupId, int total, E member) {
        if (!rankIndexNames.containsKey(name)) {
            createRankList(name, total);
        }
        commit(name, groupId, member);
    }

    @Override
    public <E extends AbstractRankMember<E>> void removeMember(String name, int groupId, E member) {
        String rankName = rankName(name);
        executor.execute(new AbstractLinkedTask() {

            @Override
            public Object getIdentity() {
                return rankName;
            }

            @Override
            protected void exec() throws Exception {

                ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(rankName);
                if (iRankList == null) {
                    return;
                }
                iRankList.removeMember(groupId, member);
            }
        });
    }

    @Override
    public void removeMemberById(String name, int groupId, String memberId) {
        String rankName = rankName(name);
        executor.execute(new AbstractLinkedTask() {
            @Override
            public Object getIdentity() {
                return rankName;
            }

            @Override
            protected void exec() throws Exception {

                ICrossRankList<? extends AbstractRankMember<?>> iRankList = rankLists.get(rankName);
                if (iRankList == null) {
                    return;
                }
                iRankList.removeMemberById(groupId, memberId);
            }
        });
    }

    @Override
    public int getTotal(String name) {
        name = rankName(name);
        ICrossRankList<? extends AbstractRankMember<?>> iRankList = rankLists.get(name);
        if (iRankList == null) {
            return 0;
        }
        return iRankList.getTotal();
    }

    @Override
    public int getSize(String name, int groupId) {
        name = rankName(name);
        ICrossRankList<? extends AbstractRankMember<?>> iRankList = rankLists.get(name);
        if (iRankList == null) {
            return 0;
        }
        return iRankList.getSize(groupId);
    }

    @Override
    public int getPages(String name, int groupId, int onePageNum) {
        name = rankName(name);
        ICrossRankList<? extends AbstractRankMember<?>> iRankList = rankLists.get(name);
        if (iRankList == null) {
            return 0;
        }
        return iRankList.getPages(groupId, onePageNum);
    }

    @Override
    public <E extends AbstractRankMember<E>> List<E> getSubListByPredicate(String name, int groupId, Predicate<? super E> predicate) {
        if (predicate == null)
            throw new RuntimeException("条件为空:" + name);
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return Collections.emptyList();
        }
        return iRankList.getSubListByPredicate(groupId, predicate);
    }

    @Override
    public <E extends AbstractRankMember<E>> List<E> getSubListByIndex(String name, int groupId, int begin, int end) {
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return Collections.emptyList();
        }
        return iRankList.getSubListByIndex(groupId, begin, end);
    }

    @Override
    public <E extends AbstractRankMember<E>> List<E> getSubListByPage(String name, int groupId, int pageIndex, int onePageNum) {
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return Collections.emptyList();
        }
        return iRankList.getSubListByPage(groupId, pageIndex, onePageNum);
    }

    @Override
    public <E extends AbstractRankMember<E>> List<E> getAllRankList(String name, int groupId) {
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return Collections.emptyList();
        }
        return iRankList.getAllRankList(groupId);
    }

    @Override
    public <E extends AbstractRankMember<E>> E getMemberById(String name, int groupId, String id) {
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return null;
        }
        return (E) iRankList.getMemberById(groupId, id);
    }

    @Override
    public <E extends AbstractRankMember<E>> E getMemberByRank(String name, int groupId, int rank) {
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return null;
        }
        return (E) iRankList.getMemberByRank(groupId, rank);
    }

    @Override
    public boolean checkRankingIsExist(String name) {
        return rankLists.keySet().stream().anyMatch(n -> n.contains(name));
    }

    @Override
    public void cleanAllRankByName(String name) {
        List<String> keyNames = new ArrayList<>();
        for (String s : rankLists.keySet()) {
            if (s.contains(name)) {
                keyNames.add(s);
            }
        }
        for (String keyName : keyNames) {
            rankLists.remove(keyName);
        }
    }

    @Override
    public <E extends AbstractRankMember<E>> void initSortedRankList(String name, int groupId, Iterable<E> members) {
        if (CollectionUtils.isEmpty(members))
            return;
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return;
        }
        iRankList.initSortedRankList(groupId, members);
    }

    @Override
    public <E extends AbstractRankMember<E>> void initSortedRankList(String name, int groupId) {
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return;
        }
        iRankList.initSortedRankList(groupId);
    }

    @Override
    public <E extends AbstractRankMember<E>> void exChangeMember(String name, int groupId, E member1, E member2) {
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return;
        }
        iRankList.exChangeMember(groupId, member1, member2);
    }

    private static CrossRankLists instance;

    public static CrossRankLists getInstance() {

        if (instance == null) {
            synchronized (RankLists.class) {
                if (instance == null) {
                    instance = new CrossRankLists();
                }
            }
        }
        return instance;
    }

    public Map<String, ICrossRankList<? extends AbstractRankMember<?>>> getRankLists() {
        return rankLists;
    }

    @Override
    public <E extends AbstractRankMember<E>> Map<Integer, ArrayList<E>> getGroupList(String name) {
        name = rankName(name);
        ICrossRankList<E> iRankList = (ICrossRankList<E>) rankLists.get(name);
        if (iRankList == null) {
            return Maps.newHashMap();
        }
        return iRankList.getGroupList();
    }
}
