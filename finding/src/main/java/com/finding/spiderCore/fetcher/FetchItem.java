package com.finding.spiderCore.fetcher;

import com.finding.spiderCore.entities.CrawlDatum;

/**
 * desc: 执行器任务单体
 * @Return:
 **/
public class FetchItem {

    private CrawlDatum datum;

    public CrawlDatum getDatum() {
        return datum;
    }

    public void setDatum(CrawlDatum datum) {
        this.datum = datum;
    }

    public FetchItem() {
    }
}
