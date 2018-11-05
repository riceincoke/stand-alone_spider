package com.myspider.core.crawlColletor.webcollector.crawldb;


import com.myspider.core.crawlColletor.webcollector.conf.DefaultConfigured;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;

public class StatusGeneratorFilter extends DefaultConfigured implements GeneratorFilter {
    @Override
    public CrawlDatum filter(CrawlDatum datum) {
        if(datum.getStatus() == CrawlDatum.STATUS_DB_SUCCESS){
            return null;
        }else{
            return datum;
        }
    }
}
