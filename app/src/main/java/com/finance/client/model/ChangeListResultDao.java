package com.finance.client.model;

import com.finance.library.model.BaseResultDO;

import java.util.List;

/**
 * User : yh
 * Date : 17/9/11
 */

public class ChangeListResultDao extends BaseResultDO{
    public List<ChangeInfoDao> getDataList() {
        return dataList;
    }

    public void setDataList(List<ChangeInfoDao> dataList) {
        this.dataList = dataList;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    private List<ChangeInfoDao> dataList;
    private int totalPage;
    private int nowPage;
    private int pageCount;
}
