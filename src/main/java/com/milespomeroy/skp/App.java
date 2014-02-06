package com.milespomeroy.skp;

import com.google.common.base.Optional;
import com.milespomeroy.skp.reader.TabReader;
import com.milespomeroy.skp.agg.AggregateUniqueHit;
import com.milespomeroy.skp.hit.UniqueHit;
import com.milespomeroy.skp.result.SearchKeywordPerformance;
import com.milespomeroy.skp.search.SearchReferrer;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * skp: Search Keyword Performance
 * Take in a tab delimited file containing a hit data set (assumes file has header row).
 * Return a tab delimited file with search keywords, ordered by total revenue descending.
 */
public class App {
    public static void main(String[] args) {
        // check for filename arg
        if(args.length == 0) {
            System.err.println("Please provide a data file for processing.");
            return;
        }

        // get file
        String filename = args[0];
        FileReader fileReader;
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            System.err.println("File was not found: " + filename);
            return;
        }

        TabReader tabReader = new TabReader(fileReader, true);
        AggregateUniqueHit aggregateUniqueHit = new AggregateUniqueHit(tabReader);
        Map<String, UniqueHit> uniqueHitsByIp;

        try {
            uniqueHitsByIp = aggregateUniqueHit.getUniqueHitsByIp();
        } catch (IOException | SuperCsvException e) {
            System.err.println("Error reading " + filename + ". Is it tab delimited hit data?");
            return;
        }

        Map<SearchReferrer, SearchKeywordPerformance> aggregateSearchReferrer = new HashMap<>();
        Optional<SearchReferrer> optSearchReferrer;
        for(UniqueHit uniqueHit : uniqueHitsByIp.values()) {
            optSearchReferrer = uniqueHit.getSearchReferrer();

            if(! optSearchReferrer.isPresent()) {
                continue;
            }

            SearchReferrer searchReferrer = optSearchReferrer.get();
            SearchKeywordPerformance skp = aggregateSearchReferrer.get(searchReferrer);
            BigDecimal revenue = uniqueHit.getRevenue();

            if(skp == null) {
                aggregateSearchReferrer.put(searchReferrer,
                                            new SearchKeywordPerformance(searchReferrer, revenue));
            } else {
                skp.addRevenue(revenue);
            }
        }

        List<SearchKeywordPerformance> results = new ArrayList<>(aggregateSearchReferrer.values());
        Collections.sort(results);

        Date today = new Date();
        SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd");
        String isoToday = isoDate.format(today);
        Path resultsFilePath = Paths.get(isoToday + "_SearchKeywordPerformance.tab");
        try {
            Writer writer = Files.newBufferedWriter(resultsFilePath, StandardCharsets.UTF_8);
            try(ICsvBeanWriter beanWriter = new CsvBeanWriter(  writer,
                                                                CsvPreference.TAB_PREFERENCE))
            {
                beanWriter.writeHeader("Search Engine Domain", "Search Keyword", "Revenue");

                for(final SearchKeywordPerformance skp : results) {
                    beanWriter.write(skp, "searchEngineDomain", "searchKeyword", "revenue");
                }
            }
        } catch (IOException | SuperCsvException e) {
            System.err.println("Error writing the results to file.");
            return;
        }

        System.out.println("Wrote results to: " + resultsFilePath.toString());
    }
}
