package com.milespomeroy.skp.hit;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * Bean corresponding to a row from a hit data set.
 * Capturing only the data that's needed.
 */
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

    public static final CellProcessor[] CELL_PROCESSORS = new CellProcessor[] {
            new ParseInt(), // hit_time_gmt
            null, // date_time
            null, // user_agent
            new NotNull(), // ip
            new Optional(), // event_list
            null, // geo_city
            null, // geo_region
            null, // geo_country
            null, // pagename
            null, // pageurl
            new Optional(), // product_list
            new Optional() // referrer
    };

    public static final String[] NAME_MAPPING = new String[] {
            "gmtTime",
            null,
            null,
            "ip",
            "eventList",
            null,
            null,
            null,
            null,
            null,
            "productList",
            "referrer"
    };
}
