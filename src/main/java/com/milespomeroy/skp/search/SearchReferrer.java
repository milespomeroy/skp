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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchReferrer that = (SearchReferrer) o;

        if (searchDomain != that.searchDomain) return false;
        if (!searchKeyword.equals(that.searchKeyword)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = searchDomain.hashCode();
        result = 31 * result + searchKeyword.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SearchReferrer{" +
                "searchDomain=" + searchDomain +
                ", searchKeyword='" + searchKeyword + '\'' +
                '}';
    }
}
