package com.milespomeroy.skp.result;

import com.milespomeroy.skp.hit.UniqueHit;
import com.milespomeroy.skp.search.SearchReferrer;

import java.math.BigDecimal;

public class SearchKeywordPerformance implements Comparable<SearchKeywordPerformance> {
    private final String searchEngineDomain;
    private final String searchKeyword;
    private BigDecimal revenue;

    public SearchKeywordPerformance(SearchReferrer referrer, BigDecimal revenue) {
        this.searchEngineDomain = referrer.getSearchDomain().getDomainName();
        this.searchKeyword = referrer.getSearchKeyword();
        this.revenue = revenue;
    }

    public SearchKeywordPerformance(String searchEngineDomain, String searchKeyword, BigDecimal revenue) {
        this.searchEngineDomain = searchEngineDomain;
        this.searchKeyword = searchKeyword;
        this.revenue = revenue;
    }

    public SearchKeywordPerformance(UniqueHit uniqueHit) {
        this.searchEngineDomain = uniqueHit.getSearchDomainEnum().getDomainName();
        this.searchKeyword = uniqueHit.getSearchKeyword();
        this.revenue = uniqueHit.getRevenue();
    }

    public void addRevenue(BigDecimal revenue) {
        this.revenue = this.revenue.add(revenue);
    }

    public String getSearchEngineDomain() {
        return searchEngineDomain;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    @Override
    public String toString() {
        return "SearchKeywordPerformance{" +
                "searchEngineDomain='" + searchEngineDomain + '\'' +
                ", searchKeyword='" + searchKeyword + '\'' +
                ", revenue=" + revenue +
                '}';
    }

    /**
     * Used for sorting by revenue descending.
     * @param that
     * @return
     */
    @Override
    public int compareTo(SearchKeywordPerformance that) {
        return - this.revenue.compareTo(that.revenue);
    }

    public static final String[] HEADER = new String[] {
            "Search Engine Domain",
            "Search Keyword",
            "Revenue"
    };

    public static final String[] NAME_MAPPING = new String[] {
            "searchEngineDomain",
            "searchKeyword",
            "revenue"
    };
}
