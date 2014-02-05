package com.milespomeroy.skp.util

import static com.milespomeroy.skp.search.SearchDomainEnum.*
import spock.lang.Specification

class HitUtilSpec extends Specification {
    def "FindSearchReferrer should work for valid URLs"() {
        when:
        def searchReferrer = HitUtil.findSearchReferrer(referrer)

        then:
        searchReferrer.isPresent()
        searchReferrer.get().getSearchDomain() == expected
        searchReferrer.get().getSearchKeyword() == search

        where:
        expected    | search        | referrer
        GOOGLE      | "Ipod"        | "http://www.google.com/search?hl=en&client=firefox-a&rls=org.mozilla%3Aen-US%3Aofficial&hs=ZzP&q=Ipod&aq=f&oq=&aqi="
        YAHOO       | "cd player"   | "http://search.yahoo.com/search?p=cd+player&toggle=1&cop=mss&ei=UTF-8&fr=yfp-t-701"
        BING        | "Zune"        | "http://www.bing.com/search?q=Zune&go=&form=QBLH&qs=n"
        BING        | ""            | "http://www.bing.com/"
    }

    def "FindSearchReferrer should return Optional.absent for non-search urls"() {
        expect:
        ! HitUtil.findSearchReferrer(referrer).isPresent()

        where:
        _ | referrer
        _ | "http://www.esshopzilla.com"
        _ | null
        _ | ""
        _ | "/"
        _ | "lakjsldk*,;l aha;l9849824"
    }

    def "FindSearchDomain should find valid search domain matches"() {
        when:
        def domain = HitUtil.findSearchDomain(hostUrlString)

        then:
        domain.isPresent()
        domain.get() == expected

        where:
        expected    | hostUrlString
        BING        | "www.bing.com"
        BING        | "bing.com"
        GOOGLE      | "www.google.com"
        YAHOO       | "search.yahoo.com"
    }

    def "FindSearchDomain should return Optional.absent for invalid search hosts"() {
        expect:
        ! HitUtil.findSearchDomain(hostUrlString).isPresent()

        where:
        _ | hostUrlString
        _ | ""
        _ | null
        _ | "milespomeroy.com"
    }

    def "FindSearchQueryParam should find the correct query param"() {
        expect:
        HitUtil.findSearchQueryParam(new URI(urlString), paramName).get() == expected

        where:
        paramName   | expected  | urlString
        "q"         | "yo yo"   | "http://google.com/?q=yo+yo"
        "search_str"| "hajo"    | "https://www.bing.com/hello/to/you?bebo=toto&search_str=hajo"
        "pea soup"  | "yea"     | "http://www.google.com/searcher?pea%20soup=yea"
    }

    def "FindSearchQueryParam should return Optional.absent when not found or invalid"() {
        expect:
        ! HitUtil.findSearchQueryParam(uri, paramName).isPresent()

        where:
        paramName   | uri
        "q"         | new URI("")
        "q"         | null
        "q"         | new URI("http://www.google.com/searcher?pea%20soup=yea")
        null        | new URI("http://google.com/?q=yo+yo")
    }

    def "FindRevenue should find correct revenue from product list"() {
        expect:
        HitUtil.findRevenue(productList) == expected

        where:
        expected | productList
        0.00     | ""
        0        | null
        250.00   | "Electronics;Zune - 32GB;1;250;"
        1004.00  | /Computers;HP Pavillion;1;1000;200|201,Office Supplies;Red Folders;4;4.00;205|206|207/
        0        | "Electronics;Ipod - Nano - 8GB;1;;"
        250.02   | "Electronics;Ipod - Nano - 8GB;1;;,Electronics;Zune - 32GB;1;250.02;"
        125.38   | "Electronics;Ipod - Nano - 8GB;1;89.75;,Electronics;Zune - 32GB;1;35.63;"
    }
}
