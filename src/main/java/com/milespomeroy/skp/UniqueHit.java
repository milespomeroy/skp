package com.milespomeroy.skp;

import java.math.BigDecimal;

public class UniqueHit {
    private String ip;
    private SearchReferrer searchReferrer;
    private BigDecimal revenue;

    /**
     * Create a UniqueHit from a Hit.
     * @param hit
     */
    public UniqueHit(Hit hit) {
        this.ip = hit.getIp();
        this.searchReferrer = findSearchReferrer(hit.getReferrer());
        this.revenue = findRevenue(hit.getProductList());
    }

    /**
     * Aggregate exiting hit with another. It is assumed that IPs are the same.
     * Uses first search referrer found, i.e. won't overwrite.
     * @param hit
     */
    public void combine(Hit hit) {
        if(this.searchReferrer == null) {
            this.searchReferrer = findSearchReferrer(hit.getReferrer());
        }
        this.revenue = this.revenue.add(findRevenue(hit.getProductList()));
    }

    /**
     * Find a search domain/keyword in the referral URL.
     * @param referralUrl
     * @return search domain/keyword as SearchReferrer or null if not found in URL.
     */
    private SearchReferrer findSearchReferrer(String referralUrl) {
        return null;
    }

    /**
     * Find a revenue in the product list.
     * @param productList
     * @return
     */
    private BigDecimal findRevenue(String productList) {
        return null;
    }
}
