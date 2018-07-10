package com.finance.client.model;

/**
 * User : yh
 * Date : 17/9/3
 */

public class MasterDao {
    private String address;
    private String attention;
    private String category;
    private String fansNumber;
    private String introduction;
    private String isVerify;
    private String logo;
    private String merchantId;
    private String name;
    private String nickName;
    private String score;
    private String signature;
    private String status;
    private String stopTime;
    private String commentNum;
    private String isman;//订购是否定满 0没满 1已满

    private String state;//0未认证，1已认证

    private String publicImage;
    private String publicitImage;
    private String publicityImage;


    public String getPublicImage() {
        return publicImage;
    }

    public void setPublicImage(String publicImage) {
        this.publicImage = publicImage;
    }

    public String getPublicitImage() {
        return publicitImage;
    }

    public void setPublicitImage(String publicitImage) {
        this.publicitImage = publicitImage;
    }

    public String getPublicityImage() {
        return publicityImage;
    }

    public void setPublicityImage(String publicityImage) {
        this.publicityImage = publicityImage;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIsman() {
        return isman;
    }

    public void setIsman(String isman) {
        this.isman = isman;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFansNumber() {
        return fansNumber;
    }

    public void setFansNumber(String fansNumber) {
        this.fansNumber = fansNumber;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(String isVerify) {
        this.isVerify = isVerify;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }
}
