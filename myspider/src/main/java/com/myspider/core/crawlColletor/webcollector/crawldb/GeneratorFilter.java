package com.myspider.core.crawlColletor.webcollector.crawldb;


import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;

public interface GeneratorFilter {
    /**
     * return datum if you want to generate datum
     * return null if you want to filter datum
     * @param datum
     * @return
     */
    public CrawlDatum filter(CrawlDatum datum);
}
