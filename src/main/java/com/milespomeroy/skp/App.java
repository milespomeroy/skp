package com.milespomeroy.skp;

import com.milespomeroy.skp.hit.Hit;
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

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.err.println("Please provide a data file for processing.");
            return;
        }

        String filename = args[0];
        FileReader fileReader;
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            System.err.println("File was not found: " + filename);
            return;
        }

        ICsvBeanReader tabReader = new CsvBeanReader(fileReader, CsvPreference.TAB_PREFERENCE);

        CellProcessor[] cellProcessors = new CellProcessor[]{
                new ParseInt(), // hit_time_gmt
                null, // date_time
                null, // user_agent
                new NotNull(), // ip
                new Optional(), // event_list
                null, // geo_city
                null, // geo_region
                null, // geo_country
                null, // pagename
                null, // pageurl
                new Optional(), // product_list
                new Optional() // referrer
        };

        String[] nameMapping = new String[] {
                "gmtTime",
                null,
                null,
                "ip",
                "eventList",
                null,
                null,
                null,
                null,
                null,
                "productList",
                "referrer"
        };

        try {
            tabReader.getHeader(true); // skip header
            Hit hit;
            while((hit = tabReader.read(Hit.class, nameMapping, cellProcessors)) != null) {
                System.out.println(hit);
            }
        } catch (IOException | SuperCsvException e) {
            System.err.println("Error reading " + filename + ". Is it tab delimited hit data?");
            return;
        }
    }
}
