package com.milespomeroy.skp.agg;

import com.milespomeroy.skp.reader.TabReader;
import com.milespomeroy.skp.hit.Hit;
import com.milespomeroy.skp.hit.UniqueHit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AggregateUniqueHit {
    private final TabReader reader;

    public AggregateUniqueHit(TabReader reader) {
        this.reader = reader;
    }

    public Map<String, UniqueHit> getUniqueHitsByIp() throws IOException {
        Map<String, UniqueHit> uniqueHitsByIp = new HashMap<>();

        if(this.reader.hasHeader()) {
            this.reader.getHeader(true); // skip header
        }

        Hit hit;
        while((hit = this.reader.read(Hit.class, Hit.NAME_MAPPING, Hit.CELL_PROCESSORS)) != null) {
            UniqueHit uniqueHit = uniqueHitsByIp.get(hit.getIp());

            if(uniqueHit == null) { // doesn't exist in map yet
                uniqueHitsByIp.put(hit.getIp(), new UniqueHit(hit));
            } else {
                uniqueHit.combine(hit);
            }
        }

        return uniqueHitsByIp;
    }
}
