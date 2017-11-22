package com.finance.client.model;

import java.util.List;

/**
 * Created by Administrator on 2017/10/13 0013.
 */

public class RenewResultDao {

    private String result;
    private String resultNote;
    private String totalPage;
    private String nowPage;
    private String pageCount;
    private List<ConcernedPublishersDao> dataList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

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

    public List<ConcernedPublishersDao> getDataList() {
        return dataList;
    }

    public void setDataList(List<ConcernedPublishersDao> dataList) {
        this.dataList = dataList;
    }
}
