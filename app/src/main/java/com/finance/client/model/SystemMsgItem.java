package com.finance.client.model;

/**
 * User : yh
 * Date : 17/8/14
 */

public class SystemMsgItem {


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



    public String getUnreadMessage() {
        return unreadMessage;
    }

    public void setUnreadMessage(String unreadMessage) {
        this.unreadMessage = unreadMessage;
    }


    private String logo;
    private String title;

    private String unreadMessage;
}
