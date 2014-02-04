package com.milespomeroy.skp;

public enum SearchDomainEnum {
    GOOGLE("google.com", "q"),
    YAHOO("yahoo.com", "p"),
    BING("bing.com", "q");

    private String name;
    private String queryParam;

    SearchDomainEnum(String name, String queryParam) {
        this.name = name;
        this.queryParam = queryParam;
    }

    public String getName() {
        return name;
    }

    public String getQueryParam() {
        return queryParam;
    }
}
