package com.milespomeroy.skp.search;

public enum SearchDomainEnum {
    GOOGLE("google.com", "q"),
    YAHOO("search.yahoo.com", "p"),
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
