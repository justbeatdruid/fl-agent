package com.cmcc.algo.common.response;

import java.io.Serializable;

public class PageInfo implements Serializable {

    private static final long serialVersionUID = -3168245848375551671L;

    /** 总页数 */
    private long pages;
    /** 总记录数 */
    private long total;
    /** 查询数据的当前页 */
    protected long pageNum;
    /** 查询时每页的数据条数 */
    protected long step;


    public PageInfo(long total) {
        this.total = total;
    }

    public PageInfo(long total, long pageNum, long step) {
        this.total = total;
        this.pageNum = pageNum;
        this.step = step;
        this.pages = (total - 1) / step + 1;
    }

    public static PageInfo page(long total) {
        return new PageInfo(total);
    }

    public static PageInfo page(long total, long pageNum, long step) {
        return new PageInfo(total, pageNum, step);
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }
}
