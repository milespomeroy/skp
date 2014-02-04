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

    def "FindSearchQueryParam"() {

    }

    def "FindRevenue"() {

    }
}
