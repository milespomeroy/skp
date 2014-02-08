package com.milespomeroy.skp.hit;

import com.google.common.base.Optional;
import com.milespomeroy.skp.search.SearchDomainEnum;
import com.milespomeroy.skp.search.SearchReferrer;
import com.milespomeroy.skp.util.HitUtil;
import org.supercsv.cellprocessor.FmtNumber;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.math.BigDecimal;

public class UniqueHit {
    private String ip;
    private SearchDomainEnum searchDomainEnum;
    private String searchKeyword;
    private BigDecimal revenue;

    public UniqueHit() {}

    /**
     * Create a UniqueHit from a Hit.
     * @param hit
     */
    public UniqueHit(Hit hit) {
        this.ip = hit.getIp();
        setSearchReferrer(hit.getReferrer());
        this.revenue = HitUtil.findRevenue(hit.getProductList());
    }

    /**
     * Aggregate exiting hit with another. It is assumed that IPs are the same.
     * Uses first search referrer found, i.e. won't overwrite.
     * @param hit
     */
    public void combine(Hit hit) {
        if(this.searchDomainEnum == null) {
            setSearchReferrer(hit.getReferrer());
        }
        this.revenue = this.revenue.add(HitUtil.findRevenue(hit.getProductList()));
    }

    public String getIp() {
        return ip;
    }

    public SearchDomainEnum getSearchDomainEnum() {
        return searchDomainEnum;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setSearchDomainEnum(SearchDomainEnum searchDomainEnum) {
        this.searchDomainEnum = searchDomainEnum;
    }

    public void setSearchDomainEnum(String searchDomainEnum) {
        this.searchDomainEnum = SearchDomainEnum.valueOf(searchDomainEnum);
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    /**
     * Find search referrer in referrer URL. If found, set searchDomainEnum and searchKeyword.
     * @param referrer
     */
    private void setSearchReferrer(String referrer) {
        Optional<SearchReferrer> searchReferrer = HitUtil.findSearchReferrer(referrer);
        if(searchReferrer.isPresent()) {
            this.searchDomainEnum = searchReferrer.get().getSearchDomain();
            this.searchKeyword = searchReferrer.get().getSearchKeyword();
        }
    }

    @Override
    public String toString() {
        return "UniqueHit{" +
                "ip='" + ip + '\'' +
                ", searchDomainEnum=" + searchDomainEnum +
                ", searchKeyword='" + searchKeyword + '\'' +
                ", revenue=" + revenue +
                '}';
    }

    public static final String[] NAME_MAPPING = new String[] {
            "ip",
            "searchDomainEnum",
            "searchKeyword",
            "revenue"
    };

    public static final CellProcessor[] READ_CELL_PROCESSORS = new CellProcessor[] {
            new NotNull(),
            new org.supercsv.cellprocessor.Optional(),
            new org.supercsv.cellprocessor.Optional(),
            new ParseBigDecimal()
    };

}
