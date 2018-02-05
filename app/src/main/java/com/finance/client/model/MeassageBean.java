package com.finance.client.model;

import java.util.List;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MeassageBean {
    private String nowPage;
    private String pageCount;
    private String result;
    private String resultNote;
//    private List<String> systemMessage;
    private String totalPage;
    private List<DataList> dataList;
    public static class DataList {
        private String category;
        private String categoryId;
        private String logo;
        private String title;
        private String unreadMessages;
        private String type;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUnreadMessages() {
            return unreadMessages;
        }

        public void setUnreadMessages(String unreadMessages) {
            this.unreadMessages = unreadMessages;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
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

//    public List<String> getSystemMessage() {
//        return systemMessage;
//    }
//
//    public void setSystemMessage(List<String> systemMessage) {
//        this.systemMessage = systemMessage;
//    }

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public List<DataList> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataList> dataList) {
        this.dataList = dataList;
    }
}
