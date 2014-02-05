package com.milespomeroy.skp.hit

import spock.lang.Specification
import static com.milespomeroy.skp.search.SearchDomainEnum.*

class UniqueHitSpec extends Specification {
    def "combine should add up revenue from productLists"() {
        given:
        def hit1 = new Hit()
        hit1.setProductList(productList1)

        def hit2 = new Hit()
        hit2.setProductList(productList2)

        def uniqueHit = new UniqueHit(hit1);

        when:
        uniqueHit.combine(hit2)

        then:
        uniqueHit.getRevenue() == expected

        where:
        expected| productList1                          | productList2
        0       | ""                                    | ""
        540     | "Electronics;Zune - 32GB;1;250;"      | "Electronics;Ipod - Touch - 32GB;1;290;"
        290     | "Electronics;Ipod - Touch - 32GB;1;;" | "Electronics;Ipod - Touch - 32GB;1;290;"
        5.05    | "blah;blah;1;;"                       | "blah;blah;1;5.05"
    }

    def "combine should pick first search referrer found"() {
        given:
        def hit1 = new Hit()
        hit1.setReferrer(referrer1)

        def hit2 = new Hit()
        hit2.setReferrer(referrer2)

        def uniqueHit = new UniqueHit(hit1);

        when:
        uniqueHit.combine(hit2)

        then:
        uniqueHit.getSearchReferrer().isPresent()
        uniqueHit.getSearchReferrer().get().getSearchDomain() == expected

        where:
        expected    | referrer1                 | referrer2
        GOOGLE      | "http://google.com"       | "http://milespomeroy.com"
        GOOGLE      | "http://google.com"       | "http://search.yahoo.com"
        YAHOO       | "http://search.yahoo.com" | ""
        BING        | "http://milespomeroy.com" | "http://www.bing.com"
    }
}
