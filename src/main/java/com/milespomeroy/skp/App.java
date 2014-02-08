package com.milespomeroy.skp;

import com.foundations.comparator.structure.IDataStructureReader;
import com.foundations.comparator.structure.RowComparator;
import com.foundations.comparator.structure.XMLStructureReader;
import com.google.code.externalsorting.ExternalSort;
import com.milespomeroy.skp.hit.Hit;
import com.milespomeroy.skp.hit.UniqueHit;
import com.milespomeroy.skp.reader.TabReader;
import com.milespomeroy.skp.result.SearchKeywordPerformance;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * skp: Search Keyword Performance
 * Take in a tab delimited file containing a hit data set (assumes file has header row).
 * Return a tab delimited file with search keywords, ordered by total revenue descending.
 */
public class App {
    public static final int MAX_TEMP_FILES_FOR_SORTING = 1024;

    public static void main(String[] args) throws Exception {
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

        // STEP 1: Slim hit data
        TabReader tabReader = new TabReader(fileReader, true);
        Path slimHitFilePath = Paths.get("slim-hit.tab");
        Writer writer = Files.newBufferedWriter(slimHitFilePath, StandardCharsets.UTF_8);
        try(ICsvBeanWriter beanWriter = new CsvBeanWriter(writer, CsvPreference.TAB_PREFERENCE))
        {
            if(tabReader.hasHeader()) {
                tabReader.getHeader(true); // skip header
            }

            Hit hit;
            while((hit = tabReader.read(Hit.class, Hit.ORIG_NAME_MAPPING, Hit.CELL_PROCESSORS)) != null) {
                beanWriter.write(hit, Hit.SLIM_NAME_MAPPING);
            }
        }

        // STEP 2: Order by ip address
        Path hitIpOrderedFilePath = Paths.get("hit-ip-ordered.tab");

        URL hitXmlUrl = App.class.getResource("hit.xml");
        File hitXmlFile = new File(hitXmlUrl.toURI());

        IDataStructureReader config = new XMLStructureReader(hitXmlFile);
        RowComparator comparator = new RowComparator(config);

        List<File> fileList = ExternalSort.sortInBatch(
                slimHitFilePath.toFile(),
                comparator,
                MAX_TEMP_FILES_FOR_SORTING,
                Charset.defaultCharset(),
                null, // use default tmp dir
                false, // remove same rows?
                0, // number of header rows
                false // use gzip?
        );
        ExternalSort.mergeSortedFiles(
                fileList,
                hitIpOrderedFilePath.toFile(),
                comparator,
                Charset.defaultCharset(),
                false
        );
        Files.delete(slimHitFilePath);

        // STEP 3: aggregate hits - unique hits
        Path uniqueHitFilePath = Paths.get("unique-hits.tab");
        try(FileReader hitFileTabReader = new FileReader(hitIpOrderedFilePath.toFile()))
        {
            ICsvBeanReader hitBeanReader = new CsvBeanReader(hitFileTabReader, CsvPreference.TAB_PREFERENCE);

            Writer uniqueHitWriter = Files.newBufferedWriter(uniqueHitFilePath, StandardCharsets.UTF_8);

            try(ICsvBeanWriter beanWriter = new CsvBeanWriter(uniqueHitWriter, CsvPreference.TAB_PREFERENCE))
            {
                Hit hit;
                UniqueHit uniqueHit = null;
                while((hit = hitBeanReader.read(Hit.class, Hit.SLIM_NAME_MAPPING, Hit.SLIM_CELL_PROCESSORS)) != null) {
                    if(uniqueHit == null) { // first iteration
                        uniqueHit = new UniqueHit(hit);
                        continue;
                    }

                    if(hit.getIp().equals(uniqueHit.getIp())) {
                        uniqueHit.combine(hit);
                    }
                    else { // next ip
                        beanWriter.write(uniqueHit, UniqueHit.NAME_MAPPING);
                        uniqueHit = new UniqueHit(hit);
                    }
                }

                // write last unique hit to file
                beanWriter.write(uniqueHit, UniqueHit.NAME_MAPPING);
            }
        }
        Files.delete(hitIpOrderedFilePath);

        // STEP 4: order by search referrer
        Path uniqueReferrerOrderedPath = Paths.get("unique-hits-referrer-ordered.tab");

        URL uniqueHitXmlUrl = App.class.getResource("unique_hit.xml");
        File uniqueHitXmlFile = new File(uniqueHitXmlUrl.toURI());

        config = new XMLStructureReader(uniqueHitXmlFile);
        comparator = new RowComparator(config);

        fileList = ExternalSort.sortInBatch(
                uniqueHitFilePath.toFile(),
                comparator,
                MAX_TEMP_FILES_FOR_SORTING,
                Charset.defaultCharset(),
                null, // use default tmp dir
                false, // remove same rows?
                0, // number of header rows
                false // use gzip?
        );
        ExternalSort.mergeSortedFiles(
                fileList,
                uniqueReferrerOrderedPath.toFile(),
                comparator,
                Charset.defaultCharset(),
                false
        );
        Files.delete(uniqueHitFilePath);

        // STEP 5: aggregate by search domain/keyword adding up revenue
        Path skpFilePath = Paths.get("search-keyword-performance.tab");
        try(FileReader uniqueHitTabReader = new FileReader(uniqueReferrerOrderedPath.toFile()))
        {
            ICsvBeanReader uniqueHitBeanReader = new CsvBeanReader(uniqueHitTabReader, CsvPreference.TAB_PREFERENCE);

            Writer skpWriter = Files.newBufferedWriter(skpFilePath, StandardCharsets.UTF_8);

            try(ICsvBeanWriter beanWriter = new CsvBeanWriter(skpWriter, CsvPreference.TAB_PREFERENCE))
            {
                UniqueHit uniqueHit;
                SearchKeywordPerformance skp = null;
                while((uniqueHit = uniqueHitBeanReader.read(UniqueHit.class, UniqueHit.NAME_MAPPING, UniqueHit.READ_CELL_PROCESSORS)) != null) {
                    if(skp == null) { // first iteration
                        skp = new SearchKeywordPerformance(uniqueHit);
                        continue;
                    }

                    if(uniqueHit.getSearchDomainEnum() == null) {
                        break; // hit the end, nulls should be sorted last
                    }

                    String searchDomain = uniqueHit.getSearchDomainEnum().getDomainName();
                    String searchKeyword = uniqueHit.getSearchKeyword();

                    if(searchKeyword == null) { // possible search domain without a keyword, just skip
                        continue;
                    }

                    if(searchDomain.equals(skp.getSearchEngineDomain())
                            && searchKeyword.equals(skp.getSearchKeyword())
                    ) {
                        skp.addRevenue(uniqueHit.getRevenue());
                    } else { // next search domain/keyword combo
                        beanWriter.write(skp, SearchKeywordPerformance.NAME_MAPPING);
                        skp = new SearchKeywordPerformance(uniqueHit);
                    }
                }

                // write last search key performance to file
                beanWriter.write(skp, SearchKeywordPerformance.NAME_MAPPING);
            }
        }
        Files.delete(uniqueReferrerOrderedPath);

        // STEP 6: order by search referrer

        // Get today's date in the format needed for the filename
        Date today = new Date();
        SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd");
        String isoToday = isoDate.format(today);

        // Write results out
        Path skpOrderedFilePath = Paths.get(isoToday + "_SearchKeywordPerformance.tab");

        // write header
        String headerLine = "Search Engine Domain\tSearch Keyword\tRevenue\n";
        try (BufferedWriter headerWriter = Files.newBufferedWriter(skpOrderedFilePath, StandardCharsets.UTF_8)) {
            headerWriter.write(headerLine, 0, headerLine.length());
        }

        URL skpXmlUrl = App.class.getResource("search_keyword_performance.xml");
        File skpXmlFile = new File(skpXmlUrl.toURI());

        config = new XMLStructureReader(skpXmlFile);
        comparator = new RowComparator(config);

        // last sort by revenue descending
        fileList = ExternalSort.sortInBatch(
                skpFilePath.toFile(),
                comparator,
                MAX_TEMP_FILES_FOR_SORTING,
                Charset.defaultCharset(),
                null, // use default tmp dir
                false, // remove same rows?
                0, // number of header rows
                false // use gzip?
        );
        ExternalSort.mergeSortedFiles(
                fileList,
                skpOrderedFilePath.toFile(),
                comparator,
                Charset.defaultCharset(),
                false, // remove same rows?
                true, // append?
                false // use gzip?
        );
        Files.delete(skpFilePath);

        System.out.println("Wrote results to: " + skpOrderedFilePath.toString());
    }
}
