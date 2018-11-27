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
package com.finding.myspider.ramSpider;

import com.finding.spiderCore.crawldb.AbstractDBManager;
import com.finding.spiderCore.entities.CrawlDatum;
import com.finding.spiderCore.entities.CrawlDatums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class RamDBManager extends AbstractDBManager {

    Logger LOG = LoggerFactory.getLogger(AbstractDBManager.class);

    private RamDB ramDB;

    public RamDBManager(RamGenerator ramGenerator) {
        super(ramGenerator);
        this.ramDB = (RamDB) getAbstractGenerator().getDataBase();
    }

    @Override
    public boolean isDBExists() {
        return true;
    }

    @Override
    public void clear(){
         getDataBase().clear();
    }

    @Override
    public void open() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {
        if (!force) {
            if (ramDB.getCrawlDB().containsKey(datum.getStatus())) {
                return;
            }
        }
        datum.setStatus(0);
        ramDB.getCrawlDB().put(datum.getStatus(), datum);
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
        for (Map.Entry<Integer, CrawlDatum> fetchEntry : ramDB.getFetchDB().entrySet()) {
            ramDB.getCrawlDB().put(fetchEntry.getKey(), fetchEntry.getValue());
        }

        /*合并link库*/
        LOG.info("merge link database");
        for (Integer key : ramDB.getLinkDB().keySet()) {
            if (!ramDB.getCrawlDB().containsKey(key)) {
                ramDB.getCrawlDB().put(key, ramDB.getLinkDB().get(key));
            }
        }

        LOG.info("end merge");
        LOG.info("crawlDB size: "+String.valueOf(ramDB.getCrawlDB().size()));
        ramDB.getFetchDB().clear();
        LOG.info("remove fetch database");
        ramDB.getLinkDB().clear();
        LOG.info("remove link database");

    }

    @Override
    public void initSegmentWriter() throws Exception {
    }

    @Override
    public synchronized void writeFetchSegment(CrawlDatum fetchDatum) throws Exception {
        ramDB.getFetchDB().put(fetchDatum.getStatus(), fetchDatum);
    }

    @Override
    public synchronized void writeParseSegment(CrawlDatums parseDatums) throws Exception {
        for (CrawlDatum datum : parseDatums) {
            ramDB.getLinkDB().put(datum.getStatus(), datum);
        }
    }

    @Override
    public void closeSegmentWriter() throws Exception {
    }
 
}
