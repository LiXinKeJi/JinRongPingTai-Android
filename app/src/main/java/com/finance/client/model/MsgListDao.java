package com.finance.client.model;

import com.finance.library.model.BaseResultDO;

import java.util.List;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MsgListDao extends BaseResultDO {

    public MsgItem getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(MsgItem systemMessage) {
        this.systemMessage = systemMessage;
    }

    public List<MsgItem> getDataList() {
        return dataList;
    }

    public void setDataList(List<MsgItem> dataList) {
        this.dataList = dataList;
    }




    private String totalPage;
    private MsgItem systemMessage;
    private List<MsgItem> dataList;
    private String result;
    private String resultNote;
    private String nowPage;
    private String pageCount;

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String getResultNote() {
        return resultNote;
    }

    @Override
    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
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
