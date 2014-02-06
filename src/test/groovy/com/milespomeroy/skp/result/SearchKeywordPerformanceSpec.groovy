package com.milespomeroy.skp.result

import com.milespomeroy.skp.search.SearchReferrer
import spock.lang.Specification
import static com.milespomeroy.skp.search.SearchDomainEnum.*;

class SearchKeywordPerformanceSpec extends Specification {
    def "test compareTo"() {
        given:
        def skp1 = new SearchKeywordPerformance(new SearchReferrer(BING, "a"), new BigDecimal("35.35"))
        def skp2 = new SearchKeywordPerformance(new SearchReferrer(referrer, keyword), new BigDecimal(revenue))

        expect:
        skp1.compareTo(skp2) == expected

        where:
        referrer | keyword | revenue | expected
        GOOGLE   | "b"     | "35.35" | 0
        BING     | "lkjl"  | "49"    | 1
        YAHOO    | "zz87"  | "2.2"   | -1
    }
}
