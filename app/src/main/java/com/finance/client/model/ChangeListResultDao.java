package com.finance.client.model;

import java.util.List;

/**
 * User : yh
 * Date : 17/9/11
 */

public class ChangeListResultDao extends BaseResultDO{
    private int totalPage;
    private int nowPage;
    private int pageCount;
    private List<ChangeInfoDao> dataList;
    public class ChangeInfoDao {
        private String type;
        private String time;
        private String money;
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }


    }
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

}
