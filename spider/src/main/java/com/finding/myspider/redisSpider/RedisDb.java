package com.finding.myspider.redisSpider;

import com.finding.spiderCore.crawldb.Idbutil.DataBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisDb implements DataBase<String> {

    @Value("redis.crawlList") private String crawlList;
    @Value("redis.fetcherList") private String fetcherList;
    @Value("redis.linkList") private String LinkList;
    @Value("redis.redirectList") private String redirectList;

    @Override
    public String getCrawlDB() {
        return crawlList;
    }

    @Override
    public String getFetchDB() {
        return fetcherList;
    }

    @Override
    public String getLinkDB() {
        return LinkList;
    }

    @Override
    public String getRedirectDB() {
        return redirectList;
    }

    @Override
    public void clear() {

    }
}
