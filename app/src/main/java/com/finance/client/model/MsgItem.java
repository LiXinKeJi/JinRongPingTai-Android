package com.finance.client.model;

/**
 * User : yh
 * Date : 17/8/14
 */

public class MsgItem {
    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(String unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    private String categoryID;
    private String logo;
    private String title;
    private String category;
    private String unreadMessages;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
