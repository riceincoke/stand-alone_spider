package com.finding.myspider.ramSpider;

import com.finding.spiderCore.crawldb.Idbutil.DataBase;
import com.finding.spiderCore.entities.CrawlDatum;
import org.springframework.stereotype.Component;

import java.util.HashMap;


/**
 * @author hu
 */
@Component
public class RamDB implements DataBase<HashMap>{
    
    private HashMap<Integer, CrawlDatum> crawlDB = new HashMap<Integer, CrawlDatum>();
    private HashMap<Integer, CrawlDatum> fetchDB = new HashMap<Integer, CrawlDatum>();
    private HashMap<Integer, CrawlDatum> linkDB = new HashMap<Integer, CrawlDatum>();
    private HashMap<Integer, String> redirectDB = new HashMap<Integer, String>();

    @Override
    public HashMap<Integer, CrawlDatum> getCrawlDB() {
        return crawlDB;
    }

    @Override
    public HashMap<Integer, CrawlDatum> getFetchDB() {
        return fetchDB;
    }

    @Override
    public HashMap<Integer, CrawlDatum> getLinkDB() {
        return linkDB;
    }

    @Override
    public HashMap<Integer, String> getRedirectDB() {
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
