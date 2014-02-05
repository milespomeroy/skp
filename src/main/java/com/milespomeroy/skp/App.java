package com.milespomeroy.skp;

import com.milespomeroy.skp.hit.Hit;
import com.milespomeroy.skp.hit.UniqueHit;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * skp: Search Keyword Performance
 * Take in a tab delimited file containing a hit data set.
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

        ICsvBeanReader tabReader = new CsvBeanReader(fileReader, CsvPreference.TAB_PREFERENCE);
        Map<String, UniqueHit> uniqueHitsByIp = new HashMap<>();

        try {
            tabReader.getHeader(true); // skip header

            Hit hit;
            while((hit = tabReader.read(Hit.class, Hit.NAME_MAPPING, Hit.CELL_PROCESSORS)) != null) {
                UniqueHit uniqueHit = uniqueHitsByIp.get(hit.getIp());

                if(uniqueHit == null) { // doesn't exist in map yet
                    uniqueHitsByIp.put(hit.getIp(), new UniqueHit(hit));
                } else {
                    uniqueHit.combine(hit);
                }
            }
        } catch (IOException | SuperCsvException e) {
            System.err.println("Error reading " + filename + ". Is it tab delimited hit data?");
            return;
        }

        for(Map.Entry<String, UniqueHit> uniqueHitByIp : uniqueHitsByIp.entrySet()) {
            System.out.println(uniqueHitByIp.getValue());
        }
    }
}
