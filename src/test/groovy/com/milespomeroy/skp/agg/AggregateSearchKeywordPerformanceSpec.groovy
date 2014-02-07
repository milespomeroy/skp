package com.milespomeroy.skp.agg

import com.milespomeroy.skp.hit.Hit
import com.milespomeroy.skp.hit.UniqueHit
import spock.lang.Specification

class AggregateSearchKeywordPerformanceSpec extends Specification {
    def "aggregate should ignore unique hits without a search referrer"() {
        given:
        def uhs = [new UniqueHit(new Hit())]
        def agg = new AggregateSearchKeywordPerformance(uhs)
        agg.aggregate()

        expect:
        agg.getSearchKeywordPerformances().size() == 0
    }

    def "aggregate should add up revenue of unique hits with the same search referrer"() {
        given:
        def hit = new Hit()
        hit.setIp("192.168.1.1")
        hit.setReferrer("http://www.bing.com/search?q=Zune&go=&form=QBLH&qs=n")
        hit.setProductList("Electronics;Zune - 32GB;1;250;")
        def hit2 = new Hit()
        hit2.setIp("127.0.0.1")
        hit2.setReferrer(referrer)
        hit2.setProductList(productList)
        def uhs = [new UniqueHit(hit), new UniqueHit(hit2)]
        def agg = new AggregateSearchKeywordPerformance(uhs)
        agg.aggregate()
        expect:

        where:
        expect  | productList           | referrer
        275.49  | "Stuff;Thing;3;25.49;"| "http://www.bing.com/search?p=blah&q=Zune"
    }
}
