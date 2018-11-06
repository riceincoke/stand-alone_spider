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

import com.finding.spiderCore.crawldb.DataBase;
import com.finding.spiderCore.entities.CrawlDatum;
import org.springframework.stereotype.Component;

import java.util.HashMap;


/**
 *
 * @author hu
 */
@Component
public class RamDB implements DataBase<HashMap>{
    
    private HashMap<String, CrawlDatum> crawlDB = new HashMap<String, CrawlDatum>();
    private HashMap<String, CrawlDatum> fetchDB = new HashMap<String, CrawlDatum>();
    private HashMap<String, CrawlDatum> linkDB = new HashMap<String, CrawlDatum>();
    private HashMap<String, String> redirectDB = new HashMap<String, String>();

    @Override
    public HashMap<String, CrawlDatum> getCrawlDB() {
        return crawlDB;
    }

    @Override
    public HashMap<String, CrawlDatum> getFetchDB() {
        return fetchDB;
    }

    @Override
    public HashMap<String, CrawlDatum> getLinkDB() {
        return linkDB;
    }

    @Override
    public HashMap<String, String> getRedirectDB() {
        return redirectDB;
    }

    @Override
    public void clear() {
        this.crawlDB.clear();
        this.fetchDB.clear();
        this.linkDB.clear();
        this.redirectDB.clear();
    }
}
