package com.finance.client.model;


import java.util.List;

/**
 * User : yh
 * Date : 17/9/3
 */

public class MasterListDao extends BaseResultDO{


    public List<MasterDao> getDataList() {
        return dataList;
    }

    public void setDataList(List<MasterDao> dataList) {
        this.dataList = dataList;
    }

    private String totalPage;
    private String nowPage;
    private List<MasterDao> dataList;
    private String pageCount;

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public String getNowPage() {
        return nowPage;
    }

    public void setNowPage(String nowPage) {
        this.nowPage = nowPage;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }
}
