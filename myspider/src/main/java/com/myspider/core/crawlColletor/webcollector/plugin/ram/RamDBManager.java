/*
 * Copyright (C) 2015 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.myspider.core.crawlColletor.webcollector.plugin.ram;


import com.myspider.core.crawlColletor.webcollector.crawldb.DBManager;
import com.myspider.core.crawlColletor.webcollector.crawldb.Generator;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 *
 * @author hu
 */
public class RamDBManager extends DBManager {

    Logger LOG = LoggerFactory.getLogger(DBManager.class);

    public RamDB ramDB;
    public RamGenerator generator=null;

    public RamDBManager(RamDB ramDB) {
        this.ramDB = ramDB;
        this.generator=new RamGenerator(ramDB);
    }

    @Override
    public boolean isDBExists() {
        return true;
    }

    @Override
    public void clear() throws Exception {
        ramDB.crawlDB.clear();
        ramDB.fetchDB.clear();
        ramDB.linkDB.clear();
        ramDB.redirectDB.clear();
    }

    @Override
    public Generator createGenerator() {
        return new RamGenerator(ramDB);
    }


    @Override
    public void open() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {
        String key = datum.key();
        if (!force) {
            if (ramDB.crawlDB.containsKey(key)) {
                return;
            }
        }
        ramDB.crawlDB.put(key, datum);
    }
    
    @Override
    public void inject(CrawlDatums datums, boolean force) throws Exception {
        for(CrawlDatum datum:datums){
            inject(datum,force);
        }
    }

    @Override
    public void merge() throws Exception {
        LOG.info("start merge");

        /*合并fetch库*/
        LOG.info("merge fetch database");
        for (Map.Entry<String, CrawlDatum> fetchEntry : ramDB.fetchDB.entrySet()) {
            ramDB.crawlDB.put(fetchEntry.getKey(), fetchEntry.getValue());
        }

        /*合并link库*/
        LOG.info("merge link database");
        for (String key : ramDB.linkDB.keySet()) {
            if (!ramDB.crawlDB.containsKey(key)) {
                ramDB.crawlDB.put(key, ramDB.linkDB.get(key));
            }
        }

        LOG.info("end merge");

        ramDB.fetchDB.clear();
        LOG.debug("remove fetch database");
        ramDB.linkDB.clear();
        LOG.debug("remove link database");

    }

    @Override
    public void initSegmentWriter() throws Exception {
    }

    @Override
    public synchronized void writeFetchSegment(CrawlDatum fetchDatum) throws Exception {
        ramDB.fetchDB.put(fetchDatum.key(), fetchDatum);
    }

    @Override
    public synchronized void writeParseSegment(CrawlDatums parseDatums) throws Exception {
        for (CrawlDatum datum : parseDatums) {
            ramDB.linkDB.put(datum.key(), datum);
        }
    }

    @Override
    public void closeSegmentWriter() throws Exception {
    }
 
}
