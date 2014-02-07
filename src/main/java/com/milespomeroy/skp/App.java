package com.milespomeroy.skp;

import com.foundations.comparator.structure.IDataStructureReader;
import com.foundations.comparator.structure.RowComparator;
import com.foundations.comparator.structure.XMLStructureReader;
import com.google.code.externalsorting.ExternalSort;
import com.milespomeroy.skp.agg.AggregateSearchKeywordPerformance;
import com.milespomeroy.skp.hit.Hit;
import com.milespomeroy.skp.reader.TabReader;
import com.milespomeroy.skp.agg.AggregateUniqueHit;
import com.milespomeroy.skp.hit.UniqueHit;
import com.milespomeroy.skp.result.SearchKeywordPerformance;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.dozer.CsvDozerBeanWriter;
import org.supercsv.io.dozer.ICsvDozerBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
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
    public static final int DEFAULTMAXTEMPFILES = 1024;

    public static void main(String[] args) throws IOException, URISyntaxException, ParserConfigurationException, SAXException {
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

        // Slim hit data
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

        // Order by ip address
        File hitIpOrderedFile = new File("hit-ip-ordered.tab");

        URL hitXmlUrl = App.class.getResource("hit.xml");
        File hitXmlFile = new File(hitXmlUrl.toURI());

        IDataStructureReader config = new XMLStructureReader(hitXmlFile);
        RowComparator comparator = new RowComparator(config);

        List<File> fileList = ExternalSort.sortInBatch( slimHitFilePath.toFile(),
                                                        comparator,
                                                        DEFAULTMAXTEMPFILES,
                                                        Charset.defaultCharset(),
                                                        null, // use default tmp dir
                                                        false, // remove same rows?
                                                        0, // number of header rows
                                                        false // use gzip?
        );
        ExternalSort.mergeSortedFiles(fileList, hitIpOrderedFile, comparator, Charset.defaultCharset(), false);

        // aggregate hits - unique hits
        try(FileReader hitFileTabReader = new FileReader(hitIpOrderedFile))
        {
            ICsvBeanReader hitBeanReader = new CsvBeanReader(hitFileTabReader, CsvPreference.TAB_PREFERENCE);

            Path uniqueHitFilePath = Paths.get("unique-hits.tab");
            Writer uniqueHitWriter = Files.newBufferedWriter(uniqueHitFilePath, StandardCharsets.UTF_8);

            try(ICsvDozerBeanWriter beanWriter = new CsvDozerBeanWriter(uniqueHitWriter, CsvPreference.TAB_PREFERENCE))
            {
                beanWriter.configureBeanMapping(UniqueHit.class, UniqueHit.FIELD_MAPPING);
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
                        beanWriter.write(uniqueHit, UniqueHit.CELL_PROCESSORS);
                        uniqueHit = new UniqueHit(hit);
                    }
                }

                // write last unique hit to file
                beanWriter.write(uniqueHit, UniqueHit.CELL_PROCESSORS);
            }
        }


//        // aggregate hit data by IP address
//        TabReader tabReader = new TabReader(fileReader, true);
//        AggregateUniqueHit aggregateUniqueHit = new AggregateUniqueHit(tabReader);
//
//        try {
//            aggregateUniqueHit.aggregate();
//        } catch (IOException | SuperCsvException e) {
//            System.err.println("Error reading " + filename + ". Is it tab delimited hit data?");
//            return;
//        }
//
//        Collection<UniqueHit> uniqueHits = aggregateUniqueHit.getUniqueHits();
//
//        // aggregate unique hits by search domain/keyword totaling revenue
//        AggregateSearchKeywordPerformance aggregateSkp = new AggregateSearchKeywordPerformance(uniqueHits);
//        aggregateSkp.aggregate();
//        Collection<SearchKeywordPerformance> skps = aggregateSkp.getSearchKeywordPerformances();
//
//        // Sort based on SearchKeywordPerformance comparator which is revenue descending
//        List<SearchKeywordPerformance> results = new ArrayList<>(skps);
////        Collections.sort(results);
//
//        // Get today's date in the format needed for the filename
//        Date today = new Date();
//        SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd");
//        String isoToday = isoDate.format(today);
//
//        // Write results out
//        Path resultsFilePath = Paths.get(isoToday + "_SearchKeywordPerformance.tab");
//        try {
//            Writer writer = Files.newBufferedWriter(resultsFilePath, StandardCharsets.UTF_8);
//            try(ICsvBeanWriter beanWriter = new CsvBeanWriter(  writer,
//                                                                CsvPreference.TAB_PREFERENCE))
//            {
//                beanWriter.writeHeader(SearchKeywordPerformance.HEADER);
//
//                for(final SearchKeywordPerformance skp : results) {
//                    beanWriter.write(skp, SearchKeywordPerformance.NAME_MAPPING);
//                    System.out.println(skp);
//                }
//            }
//        } catch (IOException | SuperCsvException e) {
//            System.err.println("Error writing the results to file.");
//            return;
//        }
//
//        System.out.println("Wrote results to: " + resultsFilePath.toString());
    }
}
