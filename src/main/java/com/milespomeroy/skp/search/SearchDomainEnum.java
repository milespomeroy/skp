package com.milespomeroy.skp.search;

public enum SearchDomainEnum {
    GOOGLE("google.com", "q"),
    YAHOO("search.yahoo.com", "p"),
    BING("bing.com", "q");

    private String domainName;
    private String queryParam;

    SearchDomainEnum(String domainName, String queryParam) {
        this.domainName = domainName;
        this.queryParam = queryParam;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getQueryParam() {
        return queryParam;
    }
}
