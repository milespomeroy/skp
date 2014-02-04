package com.milespomeroy.skp.hit;

import com.milespomeroy.skp.search.SearchReferrer;
import com.milespomeroy.skp.util.HitUtil;

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
        this.searchReferrer = HitUtil.findSearchReferrer(hit.getReferrer());
        this.revenue = HitUtil.findRevenue(hit.getProductList());
    }

    /**
     * Aggregate exiting hit with another. It is assumed that IPs are the same.
     * Uses first search referrer found, i.e. won't overwrite.
     * @param hit
     */
    public void combine(Hit hit) {
        if(this.searchReferrer == null) {
            this.searchReferrer = HitUtil.findSearchReferrer(hit.getReferrer());
        }
        this.revenue = this.revenue.add(HitUtil.findRevenue(hit.getProductList()));
    }

}
