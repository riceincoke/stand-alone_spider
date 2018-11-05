package com.myspider.core.crawlColletor.webcollector.plugin.redis;

import com.myspider.core.crawlColletor.webcollector.crawldb.Generator;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;
import org.springframework.stereotype.Component;

@Component
public class RedisDB extends Generator {
    @Override
    public CrawlDatum nextWithoutFilter() throws Exception {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
