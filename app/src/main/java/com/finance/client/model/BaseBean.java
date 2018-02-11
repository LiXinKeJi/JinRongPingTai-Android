package com.finance.client.model;

/**
 * Created by 小火
 * Create time on  2018/2/5
 * My mailbox is 1403241630@qq.com
 */

public class BaseBean {
    private String result;
    private String resultNote;
    private String orderNo;
    private String money;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
