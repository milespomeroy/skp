package com.milespomeroy.skp.agg;

import com.google.common.base.Optional;
import com.milespomeroy.skp.hit.UniqueHit;
import com.milespomeroy.skp.result.SearchKeywordPerformance;
import com.milespomeroy.skp.search.SearchReferrer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Take a unique hit data and aggregate into search keyword performance (search domain, search keyword, revenue).
 * With revenue being totaled for each search domain/keyword group.
 *
 * Usage: Create object by giving it unique hit data. Then call the aggregate function.
 * Use the get methods to obtain results.
 */
public class AggregateSearchKeywordPerformance {
    private final Collection<UniqueHit> uniqueHits;

    private Map<SearchReferrer, SearchKeywordPerformance> searchKeywordPerformanceBySearchReferrer = new HashMap<>();

    public AggregateSearchKeywordPerformance(Collection<UniqueHit> uniqueHits) {
        this.uniqueHits = uniqueHits;
    }

    public void aggregate() {
        for(UniqueHit uniqueHit : uniqueHits) {
            if(uniqueHit.getSearchDomainEnum() == null) {
                continue;
            }

            SearchReferrer searchReferrer = new SearchReferrer(uniqueHit.getSearchDomainEnum(), uniqueHit.getSearchKeyword());
            SearchKeywordPerformance skp = searchKeywordPerformanceBySearchReferrer.get(searchReferrer);
            BigDecimal revenue = uniqueHit.getRevenue();

            if(skp == null) {
                searchKeywordPerformanceBySearchReferrer.put(searchReferrer,
                        new SearchKeywordPerformance(searchReferrer, revenue));
            } else {
                skp.addRevenue(revenue);
            }
        }
    }

    public Collection<SearchKeywordPerformance> getSearchKeywordPerformances() {
        return this.searchKeywordPerformanceBySearchReferrer.values();
    }

    public Map<SearchReferrer, SearchKeywordPerformance> getSearchKeywordPerformanceBySearchReferrer() {
        return searchKeywordPerformanceBySearchReferrer;
    }
}
