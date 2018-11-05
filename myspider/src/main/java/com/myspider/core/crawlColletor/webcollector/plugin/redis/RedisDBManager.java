package com.myspider.core.crawlColletor.webcollector.plugin.redis;

import com.myspider.core.crawlColletor.webcollector.crawldb.DBManager;
import com.myspider.core.crawlColletor.webcollector.crawldb.Generator;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatums;
import com.myspider.core.redis.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisDBManager extends DBManager {
    @Autowired
   private RedisTemplate redisTemplate;
    @Autowired private SerializeUtil serializeUtil;
    @Override
    public boolean isDBExists() {
        if (redisTemplate != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() throws Exception {
        redisTemplate.delete("urls");
    }

    @Override
    public Generator createGenerator() {
        return null;
    }

    @Override
    public void open() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {

    }

    @Override
    public void inject(CrawlDatums datums, boolean force) throws Exception {

    }

    @Override
    public void merge() throws Exception {

    }

    @Override
    public void initSegmentWriter() throws Exception {

    }

    /**
     * desc:已完成抓取的任务
     * @Return:
     **/
    @Override
    public void writeFetchSegment(CrawlDatum fetchDatum) throws Exception {

    }
    /**
     * desc:从当前了任务中提取的符合条件的待抓取的任务列表
     * @Return:
     **/
    @Override
    public void writeParseSegment(CrawlDatums parseDatums) throws Exception {

    }

    @Override
    public void closeSegmentWriter() throws Exception {

    }
}
