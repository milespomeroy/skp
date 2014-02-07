package com.milespomeroy.skp;

import com.milespomeroy.skp.agg.AggregateSearchKeywordPerformance;
import com.milespomeroy.skp.reader.TabReader;
import com.milespomeroy.skp.agg.AggregateUniqueHit;
import com.milespomeroy.skp.hit.UniqueHit;
import com.milespomeroy.skp.result.SearchKeywordPerformance;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
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

        // aggregate hit data by IP address
        TabReader tabReader = new TabReader(fileReader, true);
        AggregateUniqueHit aggregateUniqueHit = new AggregateUniqueHit(tabReader);

        try {
            aggregateUniqueHit.aggregate();
        } catch (IOException | SuperCsvException e) {
            System.err.println("Error reading " + filename + ". Is it tab delimited hit data?");
            return;
        }

        Collection<UniqueHit> uniqueHits = aggregateUniqueHit.getUniqueHits();

        // aggregate unique hits by search domain/keyword totaling revenue
        AggregateSearchKeywordPerformance aggregateSkp = new AggregateSearchKeywordPerformance(uniqueHits);
        aggregateSkp.aggregate();
        Collection<SearchKeywordPerformance> skps = aggregateSkp.getSearchKeywordPerformances();

        // Sort based on SearchKeywordPerformance comparator which is revenue descending
        List<SearchKeywordPerformance> results = new ArrayList<>(skps);
        Collections.sort(results);

        // Get today's date in the format needed for the filename
        Date today = new Date();
        SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd");
        String isoToday = isoDate.format(today);

        // Write results out
        Path resultsFilePath = Paths.get(isoToday + "_SearchKeywordPerformance.tab");
        try {
            Writer writer = Files.newBufferedWriter(resultsFilePath, StandardCharsets.UTF_8);
            try(ICsvBeanWriter beanWriter = new CsvBeanWriter(  writer,
                                                                CsvPreference.TAB_PREFERENCE))
            {
                beanWriter.writeHeader(SearchKeywordPerformance.HEADER);

                for(final SearchKeywordPerformance skp : results) {
                    beanWriter.write(skp, SearchKeywordPerformance.NAME_MAPPING);
                }
            }
        } catch (IOException | SuperCsvException e) {
            System.err.println("Error writing the results to file.");
            return;
        }

        System.out.println("Wrote results to: " + resultsFilePath.toString());
    }
}
