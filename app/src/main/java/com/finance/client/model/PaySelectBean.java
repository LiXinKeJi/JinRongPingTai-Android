package com.finance.client.model;

import java.util.List;

/**
 * Created by 小火
 * Create time on  2018/1/11
 * My mailbox is 1403241630@qq.com
 */

public class PaySelectBean {
    private String isSetPassword;
    private String password;
    private String phoneNum;
    private String result;
    private String resultNote;
    private Other other;

    public Other getOther() {
        return other;
    }

    public void setOther(Other other) {
        this.other = other;
    }

    public class Other{
        private String price;
        private String id;
        private String project;

        public String getPrice() {
            return price;
        }

        public String getId() {
            return id;
        }

        public String getProject() {
            return project;
        }
    }
    public String getIsSetPassword() {
        return isSetPassword;
    }

    public void setIsSetPassword(String isSetPassword) {
        this.isSetPassword = isSetPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

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

    public List<PriceList> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<PriceList> priceList) {
        this.priceList = priceList;
    }

    private List<PriceList> priceList;
    public class PriceList{
        private String id;
        private String price;
        private String project;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }
    }

}
