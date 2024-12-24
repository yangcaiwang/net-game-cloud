
package com.common.module.internal.ranklist;

import com.common.module.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public abstract class AbstractRankMember<E extends AbstractRankMember<E>> implements IRankMember<E>, Serializable {

    transient protected final Logger log = LoggerFactory.getLogger(getClass());

    private String id; // 唯一ID

    private int rank; // 名次

    private boolean valid = true; // 是否有效,默认是有效的

    public AbstractRankMember(String id) {
        super();
        this.id = id;
    }

    public void setId(String id) {

        this.id = id;
    }

    final public String getId() {

        return id;
    }

    final public int getRank() {

        return rank;
    }

    public void setRank(int newRank) {

        this.rank = newRank;
    }

    final public boolean isValid() {

        return valid;
    }

    final void setValid(boolean valid) {

        this.valid = valid;
    }

    @Override
    final public int compareTo(E e) {

        if (!this.isValid() && !e.isValid())
            return 0;
        if (!this.isValid())
            return 1;
        if (!e.isValid())
            return -1;
        return compare2(e);
    }

    /**
     * 比较成绩确定顺序
     *
     * @param e
     * @return
     */
    protected abstract int compare2(E e);

    /**
     * 比较成绩确定顺序
     *
     */
    public int compare2(E o1, E o2) {
		return 0;
    }

    @Override
    public String toString() {
        return StringUtils.toString(this);
    }
}
