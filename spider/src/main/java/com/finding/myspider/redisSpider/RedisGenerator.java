package com.finding.myspider.redisSpider;

import com.finding.myspider.spiderTools.SerializeUtil;
import com.finding.spiderCore.crawldb.AbstractGenerator;
import com.finding.spiderCore.crawldb.Idbutil.GeneratorFilter;
import com.finding.spiderCore.entities.CrawlDatum;
import com.finding.spiderCore.spiderConfig.DefaultConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 一杯咖啡
 */
@Component
public class RedisGenerator extends AbstractGenerator<String> {
    private static Logger log = LoggerFactory.getLogger(DefaultConfigImp.class);

    @Autowired
    private SerializeUtil serializeUtil;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public RedisGenerator(RedisDb redisDb, GeneratorFilter filter) {
        this.dataBase = redisDb;
        this.filter = filter;
    }

    /**
     * desc: 数据库提取任务
     **/
    @Override
    public CrawlDatum nextWithoutFilter() throws Exception {
        String datumString;
        CrawlDatum datum = null;
        String parse = dataBase.getFetchDB();
        datumString = redisTemplate.opsForList().leftPop(parse);
        //log.info("解析数据库任务提取 : " + datumstr);
        if (datumString != null) {
            datum = (CrawlDatum) serializeUtil.deserializeToObject(datumString);
        }
        return datum;
    }

    @Override
    public void close() throws Exception {
    }

    public SerializeUtil getSerializeUtil() {
        return serializeUtil;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }
}
