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
package com.myspider.core.crawlColletor.webcollector.plugin.berkeley;

import com.myspider.core.crawlColletor.webcollector.crawldb.DBManager;
import com.myspider.core.crawlColletor.webcollector.crawldb.Generator;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatums;
import com.myspider.core.crawlColletor.webcollector.util.CrawlDatumFormater;
import com.myspider.core.crawlColletor.webcollector.util.FileUtils;
import com.sleepycat.je.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author hu
 */
public class BerkeleyDBManager extends DBManager {

    Logger LOG = LoggerFactory.getLogger(BerkeleyDBManager.class);

    Environment env;
    String crawlPath;
    BerkeleyGenerator generator = null;

    public BerkeleyDBManager(String crawlPath) {
        this.crawlPath = crawlPath;
    }

    public void list() throws Exception {
        if (env == null) {
            open();
        }
        Cursor cursor = null;
        Database crawldbDatabase = env.openDatabase(null, "crawldb", BerkeleyDBUtils.defaultDBConfig);
        cursor = crawldbDatabase.openCursor(null, CursorConfig.DEFAULT);
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry value = new DatabaseEntry();

        while (cursor.getNext(key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
            try {
                CrawlDatum datum = BerkeleyDBUtils.createCrawlDatum(key, value);
                System.out.println(CrawlDatumFormater.datumToString(datum));
            } catch (Exception ex) {
                LOG.info("Exception when generating", ex);
                continue;
            }
        }

    }

    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {
        Database database = env.openDatabase(null, "crawldb", BerkeleyDBUtils.defaultDBConfig);
        DatabaseEntry key = BerkeleyDBUtils.strToEntry(datum.key());
        DatabaseEntry value = new DatabaseEntry();
        if (!force) {
            if (database.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                database.close();
                return;
            }
        }
        value = BerkeleyDBUtils.strToEntry(CrawlDatumFormater.datumToJsonStr(datum));
        database.put(null, key, value);
        database.close();
    }

    @Override
    public void inject(CrawlDatums datums, boolean force) throws Exception {
        Database database = env.openDatabase(null, "crawldb", BerkeleyDBUtils.defaultDBConfig);
        for (int i = 0; i < datums.size(); i++) {
            CrawlDatum datum = datums.get(i);
            DatabaseEntry key = BerkeleyDBUtils.strToEntry(datum.key());
            DatabaseEntry value = new DatabaseEntry();
            if (!force) {
                if (database.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                    continue;
                }
            }
            value = BerkeleyDBUtils.strToEntry(CrawlDatumFormater.datumToJsonStr(datum));
            database.put(null, key, value);
        }
        database.close();
    }

    @Override
    public void open() throws Exception {
        File dir = new File(crawlPath);
        if (!dir.exists()) {
            dir.mkdirs();
            LOG.info("建立url数据库"+crawlPath+" 成功,路径："+dir.getAbsolutePath());
        }
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);

        env = new Environment(dir, environmentConfig);
    }

    @Override
    public void close() throws Exception {
        env.close();
    }

    public int BUFFER_SIZE = 1;
    Database fetchDatabase = null;
    Database linkDatabase = null;

    AtomicInteger count_fetch = new AtomicInteger(0);
    AtomicInteger count_link = new AtomicInteger(0);

    @Override
    public void initSegmentWriter() throws Exception {
        fetchDatabase = env.openDatabase(null, "fetch", BerkeleyDBUtils.defaultDBConfig);
        linkDatabase = env.openDatabase(null, "link", BerkeleyDBUtils.defaultDBConfig);

        count_fetch = new AtomicInteger(0);
        count_link = new AtomicInteger(0);
    }

    @Override
    public void writeFetchSegment(CrawlDatum fetchDatum) throws Exception {
        BerkeleyDBUtils.writeDatum(fetchDatabase, fetchDatum);
    }


    @Override
    public void writeParseSegment(CrawlDatums parseDatums) throws Exception {
        for (CrawlDatum datum : parseDatums) {
            BerkeleyDBUtils.writeDatum(linkDatabase, datum);
        }
    }

    @Override
    public void closeSegmentWriter() throws Exception {
        if (fetchDatabase != null) {
            fetchDatabase.close();
        }
        if (linkDatabase != null) {
            linkDatabase.close();
        }
       
    }

    @Override
    public void merge() throws Exception {
        LOG.info("start merge");
        Database crawldbDatabase = env.openDatabase(null, "crawldb", BerkeleyDBUtils.defaultDBConfig);
        /*合并fetch库*/
        LOG.info("merge fetch database");
        Database fetchDatabase = env.openDatabase(null, "fetch", BerkeleyDBUtils.defaultDBConfig);
        Cursor fetchCursor = fetchDatabase.openCursor(null, null);
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry value = new DatabaseEntry();
        while (fetchCursor.getNext(key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
            crawldbDatabase.put(null, key, value);
        }
        fetchCursor.close();
        fetchDatabase.close();
        /*合并link库*/
        LOG.info("merge link database");
        Database linkDatabase = env.openDatabase(null, "link", BerkeleyDBUtils.defaultDBConfig);
        Cursor linkCursor = linkDatabase.openCursor(null, null);
        while (linkCursor.getNext(key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
            if (!(crawldbDatabase.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS)) {
                crawldbDatabase.put(null, key, value);
            }
        }
        linkCursor.close();
        linkDatabase.close();
        LOG.info("end merge");
        crawldbDatabase.close();

        env.removeDatabase(null, "fetch");
        LOG.debug("remove fetch database");
        env.removeDatabase(null, "link");
        LOG.debug("remove link database");

    }


    @Override
    public boolean isDBExists() {
        File dir = new File(crawlPath);
        return dir.exists();
    }

    @Override
    public void clear() throws Exception {
        File dir = new File(crawlPath);
        if (dir.exists()) {
            FileUtils.deleteDir(dir);
        }
    }

    @Override
    public Generator createGenerator() {
        return new BerkeleyGenerator(env);

//        if(generator==null){
//             generator = new BerkeleyGenerator(env);
//        }
//        return generator;
    }

}
