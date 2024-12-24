package com.common.module.internal.ranklist;

import com.common.module.internal.event.EventBusesImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

/**
 * 维护现有的名次 上下浮动排序
 *
 * @param <E>
 */
public class RankList<E extends AbstractRankMember<E>> implements IRankList<E> {

    transient protected final Logger log = LoggerFactory.getLogger(getClass());

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private final String name;

    private final int total;

    private final HashMap<String, E> map;

    private final ArrayList<E> list;

    RankList(String name, int total) {
        super();
        this.name = name;
        this.total = total;
        this.map = Maps.newHashMapWithExpectedSize(total <= DEFAULT_SIZE ? 8 : 16);
        this.list = total <= DEFAULT_SIZE ? Lists.newArrayListWithCapacity(total) : Lists.newArrayListWithCapacity(DEFAULT_SIZE);
    }

    @Override
    public void initMembers(Iterable<E> members) {

        List<E> initList = Lists.newArrayList(members);
        SortUtils.quickSort(initList, 0, initList.size() - 1);
        for (int i = 0; i < initList.size(); i++) {
            int index = i;
            E e = initList.get(index);
            e.setRank(index + 1);
        }
        for (int i = initList.size(); --i >= total; ) {
            initList.remove(i);
        }
        list.clear();
        map.clear();
        list.addAll(initList);
        list.forEach(e -> map.put(e.getId(), e));
    }

    public void removeMember(E member) {

        if (member != null) {
            member.setValid(false);
            commit(member);
        }
    }

    public void removeMemberById(String memberId) {

        E member = getMemberById(memberId);
        if (member != null)
            removeMember(member);
    }

    public int commit(E member) {
        lock.writeLock().lock();
        try {
            E same_old = map.get(member.getId());
            if (same_old != null) {// 已存在
                if (!member.equals(same_old)) {// 如果使用了新对象提交,但是key是存在的,要先替换
                    boolean down = member.compareTo(same_old) > 0;// 成绩下降
                    map.put(member.getId(), member);
                    list.set(same_old.getRank() - 1, member);
                    member.setRank(same_old.getRank());
                    return !down ? pushUp(member) : pushDown(member);
                } else {// 还是旧成员被再次提交
                    int changed = pushUp(member);
                    if (changed == 0) {
                        changed = pushDown(member);
                    }
                    return changed;
                }
            } else {// 不存在
                if (list.size() < total) {
                    map.put(member.getId(), member);
                    list.add(member);
                    member.setRank(list.size());
                    return pushUp(member);
                } else {
                    E last = list.get(list.size() - 1);
                    if (member.compareTo(last) < 0) {// 成绩比最后一名高
                        map.remove(last.getId());
                        map.put(member.getId(), member);
                        list.set(last.getRank() - 1, member);
                        member.setRank(last.getRank());
                        return pushUp(member);
                    } else {
                        return 0;
                    }
                }
            }
        } finally {
            for (int i = list.size(); --i >= 0; ) {
                if (list.get(i).isValid())
                    break;
                map.remove(list.remove(i).getId());
            }
            lock.writeLock().unlock();
        }
    }

    private int pushUp(E member) {
        Map<RankChangedEvent.RankModifyType, Map<String, AtomicInteger>> map = Maps.newHashMap();

        int count = 0;
        int selfIndex = member.getRank() - 1;
        for (int index = selfIndex; --index >= 0; ) {
            E before = list.get(index);
            int beforeRank = before.getRank();
            if (member.compareTo(before) >= 0) {
                break;
            }
            member.setRank(beforeRank);
            before.setRank(beforeRank + 1);
            list.set(index, member);
            map.computeIfAbsent(RankChangedEvent.RankModifyType.UP, v -> Maps.newHashMap()).computeIfAbsent(member.getId(), v -> new AtomicInteger()).incrementAndGet();
            list.set(index + 1, before);
            map.computeIfAbsent(RankChangedEvent.RankModifyType.DOWN, v -> Maps.newHashMap()).computeIfAbsent(before.getId(), v -> new AtomicInteger()).incrementAndGet();
            count++;
        }

        map.forEach((modifyType, m) -> {
            m.forEach((id, updRank) -> {
                E mem = getMemberById(id);
                if (mem != null) {
                    EventBusesImpl.getInstance().asyncPublish(name, new RankChangedEvent(name, id, updRank.get(), mem.getRank(), modifyType));
                }
            });
        });

        return count;
    }

    private int pushDown(E member) {
        Map<RankChangedEvent.RankModifyType, Map<String, AtomicInteger>> map = Maps.newHashMap();

        int count = 0;
        int selfIndex = member.getRank() - 1;
        for (int index = selfIndex; ++index < list.size(); ) {
            E next = list.get(index);
            int nextRank = next.getRank();
            if (member.compareTo(next) <= 0) {
                break;
            }
            member.setRank(nextRank);
            next.setRank(nextRank - 1);
            list.set(index - 1, next);
            map.computeIfAbsent(RankChangedEvent.RankModifyType.UP, v -> Maps.newHashMap()).computeIfAbsent(next.getId(), v -> new AtomicInteger()).incrementAndGet();
            list.set(index, member);
            map.computeIfAbsent(RankChangedEvent.RankModifyType.DOWN, v -> Maps.newHashMap()).computeIfAbsent(member.getId(), v -> new AtomicInteger()).incrementAndGet();
            count++;
        }

        map.forEach((modifyType, m) -> {
            m.forEach((id, updRank) -> {
                E mem = getMemberById(id);
                if (mem != null) {
                    EventBusesImpl.getInstance().asyncPublish(name, new RankChangedEvent(name, id, updRank.get(), mem.getRank(), modifyType));
                }
            });
        });

        return count;
    }

    public E getMemberById(String id) {

        lock.readLock().lock();
        try {
            return map.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public E getMemberByRank(int rank) {

        lock.readLock().lock();
        try {
            return rank < 1 || rank > list.size() ? null : list.get(rank - 1);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getPages(int num) {

        int length = getSize();
        if (length == 0) {
            return 0;
        }
        if (length <= num) {
            return 1;
        }
        return (length % num == 0) ? (length / num) : ((length / num) + 1);
    }

    public List<E> getSubListByPredicate(Predicate<? super E> predicate) {

        lock.readLock().lock();
        try {
            List<E> result = Lists.newArrayList();
            for (int i = 0; i < list.size(); i++) {
                E member = list.get(i);
                if (predicate.test(member)) {
                    result.add(member);
                }
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<E> getSubListByIndex(int fromIndex, int toIndex) {

        lock.readLock().lock();
        try {
            if (fromIndex < 0)
                fromIndex = 0;
            if (fromIndex > list.size())
                fromIndex = list.size();
            if (toIndex < 0)
                toIndex = 0;
            if (toIndex > list.size())
                toIndex = list.size();
            int begin = 0;
            int end = 0;
            if (fromIndex > toIndex) {
                begin = toIndex;
                end = fromIndex;
            } else {
                begin = fromIndex;
                end = toIndex;
            }
            List<E> result = list.subList(begin, end);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<E> getSubListByPage(int page, int num) {

        lock.readLock().lock();
        try {
            int pages = getPages(num);
            if (page > pages || page < 1) {
                return new ArrayList<>();
            }
            List<E> result = new ArrayList<>(num);
            int pageIndex = page - 1;
            for (int i = pageIndex * num; i < pageIndex * num + num; i++) {
                if (i >= list.size()) {
                    break;
                }
                result.add(list.get(i));
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<E> getAllRankList() {
        lock.readLock().lock();
        try {
            List<E> result = new ArrayList<>(list);
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getName() {

        return name;
    }

    public int getSize() {

        lock.readLock().lock();
        try {
            return list.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getTotal() {

        return total;
    }

    @Override
    public void initSortedRankList() {
        lock.readLock().lock();
        try {
            map.clear();
            list.stream().sorted((o1, o2) -> o1.compare2(o1, o2));
            int index = 1;
            for (E e : list) {
                e.setRank(index);
                index++;
                map.put(e.getId(), e);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void initSortedRankList(Iterable<E> members) {

    }

    @Override
    public void exChangeMember(E member1, E member2) {
        lock.writeLock().lock();
        try {
            int rank1 = member1.getRank();
            int rank2 = member2.getRank();
            if (list.get(rank1 - 1) == null || list.get(rank2 - 1) == null) return;
            member1.setRank(rank2);
            member2.setRank(rank1);
            list.set(rank1 - 1, member2);
            list.set(rank2 - 1, member1);
            int modify = Math.abs(rank1 - rank2);
            if (rank1 > rank2) {
                EventBusesImpl.getInstance().syncPublish(new RankChangedEvent(getName(), member1.getId(), modify, rank2, RankChangedEvent.RankModifyType.UP));
                EventBusesImpl.getInstance().syncPublish(new RankChangedEvent(getName(), member2.getId(), modify, rank1, RankChangedEvent.RankModifyType.DOWN));
            } else {
                EventBusesImpl.getInstance().syncPublish(new RankChangedEvent(getName(), member1.getId(), modify, rank2, RankChangedEvent.RankModifyType.DOWN));
                EventBusesImpl.getInstance().syncPublish(new RankChangedEvent(getName(), member2.getId(), modify, rank1, RankChangedEvent.RankModifyType.UP));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
