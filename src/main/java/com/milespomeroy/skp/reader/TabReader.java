package com.milespomeroy.skp.reader;

import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.Reader;

public class TabReader extends CsvBeanReader {
    private final boolean header;

    /**
     * Create CsvBeanReader for reading tab files.
     * @param reader
     * @param header Does what is being read, file/string, have a header on the first line?
     */
    public TabReader(Reader reader, boolean header) {
        super(reader, CsvPreference.TAB_PREFERENCE);
        this.header = header;
    }

    public boolean hasHeader() {
        return header;
    }
}
