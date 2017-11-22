package com.finance.client.model;

import com.finance.library.model.BaseResultDO;

import java.util.List;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MsgInfoListDao extends BaseResultDO{
    public List<MsgInfoDao> getDataList() {
        return dataList;
    }

    public void setDataList(List<MsgInfoDao> dataList) {
        this.dataList = dataList;
    }

    private List<MsgInfoDao> dataList;

    private String totalPage;

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }
}
