package com.milespomeroy.skp.agg

import com.milespomeroy.skp.reader.TabReader
import spock.lang.Specification

class AggregateUniqueHitSpec extends Specification {
    def "should aggregate correct total revenue amount when two separate purchases are made"() {
        setup:
        def agg = new AggregateUniqueHit(new TabReader(new StringReader(tab), false))

        expect:
        agg.getUniqueHitsByIp().get(ip).getRevenue() == totalRev

        where:
        totalRev| ip            | tab
        440.00  | "23.8.61.21"  | """\
1254034666\t2009-09-27 06:57:46\t"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_4_11; en) AppleWebKit/525.27.1 (KHTML, like Gecko) Version/3.2.1 Safari/525.27.1"\t23.8.61.21\t1\tRochester\tNY\tUS\tOrder Complete\thttps://www.esshopzilla.com/checkout/?a=complete\tElectronics;Zune - 32GB;1;250;\thttps://www.esshopzilla.com/checkout/?a=confirm
1254034963\t2009-09-27 07:02:43\t"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_4_11; en) AppleWebKit/525.27.1 (KHTML, like Gecko) Version/3.2.1 Safari/525.27.1"\t23.8.61.21\t1\tDuncan\tOK\tUS\tOrder Complete\thttps://www.esshopzilla.com/checkout/?a=complete\tElectronics;Ipod - Nano - 8GB;1;190;\thttps://www.esshopzilla.com/checkout/?a=confirm"""
    }
}
