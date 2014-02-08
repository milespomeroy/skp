package com.milespomeroy.skp.util;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.milespomeroy.skp.search.SearchDomainEnum;
import com.milespomeroy.skp.search.SearchReferrer;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HitUtil {
    /**
     * Find a search domain/keyword in the referral URL.
     * @param referralUrl
     * @return search domain/keyword as SearchReferrer or Optional.absent if not found in URL.
     */
    public static Optional<SearchReferrer> findSearchReferrer(String referralUrl) {
        URI url;

        try {
            url = new URI(referralUrl);
        } catch (URISyntaxException | NullPointerException e) {
            return Optional.absent();
        }

        Optional<SearchDomainEnum> searchDomain = findSearchDomain(url.getHost());
        if(!searchDomain.isPresent()) {
            return Optional.absent();
        }

        SearchDomainEnum searchDomainEnum = searchDomain.get();
        Optional<String> searchParam = findSearchQueryParam(url, searchDomainEnum.getQueryParam());

        SearchReferrer sr = new SearchReferrer(searchDomainEnum, searchParam.or(""));

        return Optional.of(sr);
    }

    /**
     * Find a search domain enum given a host string like 'www.google.com'.
     * @param host
     * @return
     */
    public static Optional<SearchDomainEnum> findSearchDomain(String host) {
        if(Strings.isNullOrEmpty(host)) {
            return Optional.absent();
        }

        if(host.startsWith("www")) {
            host = host.substring(4);
        }

        for(SearchDomainEnum searchDomain : SearchDomainEnum.values()) {
            if(searchDomain.getDomainName().equals(host)) {
                return Optional.of(searchDomain);
            }
        }

        return Optional.absent();
    }

    /**
     * Find a search query param value from a uri.
     * @param uri URI possibly containing a search query param.
     * @param searchQueryParamName The search domain type for the uri.
     * @return The first value found with the matching searchQueryParamName given. Optional.absent is none found.
     */
    public static Optional<String> findSearchQueryParam(URI uri, String searchQueryParamName) {
        if(uri == null) {
            return Optional.absent();
        }

        List<NameValuePair> queryParams = URLEncodedUtils.parse(uri, Charset.defaultCharset().name());

        for(NameValuePair param : queryParams) {
            if(param.getName().equals(searchQueryParamName)) {
                return Optional.of(param.getValue());
            }
        }

        return Optional.absent();
    }

    /**
     * Find a revenue in the product list.
     * @param productList
     * @return Total of revenue found in productList. 0.00 if not found.
     */
    public static BigDecimal findRevenue(String productList) {
        BigDecimal totalRevenue = new BigDecimal(0);

        if(Strings.isNullOrEmpty(productList)) {
            return totalRevenue; // zero
        }

        String[] products = productList.split(",");
        for(String product : products) {
            String[] productMeta = product.split(";");

            if(productMeta.length > 3) {
                BigDecimal revenue = new BigDecimal(productMeta[3]);
                totalRevenue = totalRevenue.add(revenue);
            }
        }

        return totalRevenue;
    }

    /**
     * Remove null items from an array.
     * @param items
     * @return An ArrayList of items without nulls.
     */
    public static <T> List<T> removeNulls(T[] items) {
        List<T> unnulled = new ArrayList<>();

        for(T item : items) {
            if(item != null) {
                unnulled.add(item);
            }
        }
        return unnulled;
    }
}
