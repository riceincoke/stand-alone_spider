package com.finding.myspider.redisSpider;

import com.finding.myspider.spiderTools.SerializeUtil;
import com.finding.spiderCore.crawldb.DataBase;
import com.finding.spiderCore.crawldb.Generator;
import com.finding.spiderCore.crawldb.GeneratorFilter;
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
public class RedisGenerator extends Generator {
    private static Logger log = LoggerFactory.getLogger(DefaultConfigImp.class);

    @Autowired private SerializeUtil serializeUtil;
    @Resource  private RedisTemplate redisTemplate;

    public RedisGenerator(RedisDb redisDb,GeneratorFilter filter){
        this.dataBase = redisDb;
        this.filter = filter;
    }

    @Override
    public CrawlDatum nextWithoutFilter() throws Exception {
        //将URL字符串反序列化为Url;
        String datumstr  = (String) redisTemplate.opsForList().leftPop(dataBase.getCrawlDB());
        CrawlDatum datum = (CrawlDatum) serializeUtil.deserializeToObject(datumstr);
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
