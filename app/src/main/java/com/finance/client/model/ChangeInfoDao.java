package com.finance.client.model;

/**
 * User : yh
 * Date : 17/9/11
 */

public class ChangeInfoDao {
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

    private String type;
    private String time;
    private String money;
}
