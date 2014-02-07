package com.milespomeroy.skp.hit;

import com.google.common.base.Optional;
import com.milespomeroy.skp.search.SearchReferrer;
import com.milespomeroy.skp.util.HitUtil;
import org.supercsv.cellprocessor.FmtNumber;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.math.BigDecimal;

public class UniqueHit {
    private String ip;
    private Optional<SearchReferrer> searchReferrer;
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
        if(!this.searchReferrer.isPresent()) {
            this.searchReferrer = HitUtil.findSearchReferrer(hit.getReferrer());
        }
        this.revenue = this.revenue.add(HitUtil.findRevenue(hit.getProductList()));
    }

    public String getIp() {
        return ip;
    }

    public Optional<SearchReferrer> getSearchReferrer() {
        return searchReferrer;
    }

    public SearchReferrer getSearchReferrerIfExists() {
        if(this.searchReferrer.isPresent()) {
            return this.searchReferrer.get();
        }

        return null;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    @Override
    public String toString() {
        return "UniqueHit{" +
                "ip='" + ip + '\'' +
                ", searchReferrer=" + searchReferrer +
                ", revenue=" + revenue +
                '}';
    }

    public static final String[] NAME_MAPPING = new String[] {
            "ip",
            "searchReferrer",
            "revenue"
    };

    public static final String[] FIELD_MAPPING = new String[] {
            "ip",
            "searchReferrerIfExists.searchDomain",
            "searchReferrerIfExists.searchKeyword",
            "revenue"
    };

    public static final CellProcessor[] CELL_PROCESSORS = new CellProcessor[] {
            new NotNull(),
            new org.supercsv.cellprocessor.Optional(),
            new org.supercsv.cellprocessor.Optional(),
            new FmtNumber("#.##")
    };

}
