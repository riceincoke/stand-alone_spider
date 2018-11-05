package com.finding.myspider.ramSpider;

import com.finding.spiderCore.crawldb.DBManager;
import com.finding.spiderCore.crawler.AutoParseCrawler;

/**
 * 基于内存的Crawler插件，适合一次性爬取，并不具有断点爬取功能
 * 长期任务请使用BreadthCrawler
 */
public abstract class RamCrawler extends AutoParseCrawler {

    public RamCrawler( ) {
    }

    public void start() throws Exception{
        start(Integer.MAX_VALUE);
    }

}
