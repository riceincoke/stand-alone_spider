package com.myspider.core.crawlColletor.webcollector.plugin.redis;

import com.myspider.core.crawlColletor.webcollector.crawldb.Generator;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;

public class RedisGenerator extends Generator {

    @Override
    public CrawlDatum nextWithoutFilter() throws Exception {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
