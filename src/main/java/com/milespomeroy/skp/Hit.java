package com.milespomeroy.skp;

public class Hit {
    private String ip;
    private int gmtTime;
    private String eventList;
    private String productList;
    private String referrer;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getGmtTime() {
        return gmtTime;
    }

    public void setGmtTime(int gmtTime) {
        this.gmtTime = gmtTime;
    }

    public String getEventList() {
        return eventList;
    }

    public void setEventList(String eventList) {
        this.eventList = eventList;
    }

    public String getProductList() {
        return productList;
    }

    public void setProductList(String productList) {
        this.productList = productList;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    @Override
    public String toString() {
        return "Hit{" +
                "ip='" + ip + '\'' +
                ", gmtTime=" + gmtTime +
                ", eventList='" + eventList + '\'' +
                ", productList='" + productList + '\'' +
                ", referrer='" + referrer + '\'' +
                '}';
    }
}
