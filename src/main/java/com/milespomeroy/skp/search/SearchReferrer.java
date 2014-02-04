package com.milespomeroy.skp.search;

public class SearchReferrer {
    private final SearchDomainEnum searchDomain;
    private final String searchKeyword;

    public SearchReferrer(SearchDomainEnum searchDomain, String searchKeyword) {
        this.searchDomain = searchDomain;
        this.searchKeyword = searchKeyword;
    }

    public SearchDomainEnum getSearchDomain() {
        return searchDomain;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }
}
