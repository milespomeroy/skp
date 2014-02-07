package com.milespomeroy.skp.agg;

import com.milespomeroy.skp.reader.TabReader;
import com.milespomeroy.skp.hit.Hit;
import com.milespomeroy.skp.hit.UniqueHit;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Take a tab delimited file of hit data and aggregate into unique hits based on IP address.
 *
 * Usage: Create object by giving it a TabReader. Then call the aggregate function.
 * Use the get methods to obtain results.
 */
public class AggregateUniqueHit {
    private final TabReader reader;

    private Map<String, UniqueHit> uniqueHitsByIp = new HashMap<>();

    public AggregateUniqueHit(TabReader reader) {
        this.reader = reader;
    }

    /**
     *
     * @throws IOException
     */
    public void aggregate() throws IOException {
        if(this.reader.hasHeader()) {
            this.reader.getHeader(true); // skip header
        }

        Hit hit;
        while((hit = this.reader.read(Hit.class, Hit.ORIG_NAME_MAPPING, Hit.CELL_PROCESSORS)) != null) {
            UniqueHit uniqueHit = uniqueHitsByIp.get(hit.getIp());

            if(uniqueHit == null) { // doesn't exist in map yet
                uniqueHitsByIp.put(hit.getIp(), new UniqueHit(hit));
            } else {
                uniqueHit.combine(hit);
            }
        }
    }

    public Collection<UniqueHit> getUniqueHits() {
        return this.uniqueHitsByIp.values();
    }

    public Map<String, UniqueHit> getUniqueHitsByIp() {
        return this.uniqueHitsByIp;
    }
}
