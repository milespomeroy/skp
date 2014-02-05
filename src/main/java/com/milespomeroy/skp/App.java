package com.milespomeroy.skp;

import com.milespomeroy.skp.reader.TabReader;
import com.milespomeroy.skp.agg.AggregateUniqueHit;
import com.milespomeroy.skp.hit.UniqueHit;
import org.supercsv.exception.SuperCsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

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

        for(Map.Entry<String, UniqueHit> uniqueHitByIp : uniqueHitsByIp.entrySet()) {
            System.out.println(uniqueHitByIp.getValue());
        }
    }
}
