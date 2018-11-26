package com.finding.myspider.redisSpider;

import com.finding.myspider.spiderTools.SerializeUtil;
import com.finding.spiderCore.crawldb.AbstractDBManager;
import com.finding.spiderCore.crawldb.Idbutil.DataBase;
import com.finding.spiderCore.entities.CrawlDatum;
import com.finding.spiderCore.entities.CrawlDatums;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * desc:连接操作redis类
 **/
@Component
public class RedisManager extends AbstractDBManager {
    private static final Logger log = Logger.getLogger(RedisManager.class);

    private DataBase<String> redisDb;
    private RedisTemplate<String, String> redisTemplate;
    private SerializeUtil serializeUtil;

    public RedisManager(RedisDb redisDb, RedisGenerator generator) {
        super(generator);
        this.redisDb = redisDb;
        this.serializeUtil = generator.getSerializeUtil();
        this.redisTemplate = generator.getRedisTemplate();
    }

    @Override
    public boolean isDBExists() {
        if (redisTemplate != null) {
            return true;
        }
        return false;
    }

    @Override
    public void clear() throws Exception {
        redisTemplate.delete("");
    }

    @Override
    public void open() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    /**
     * desc: 任务强制注入
     **/
    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {
        flushdb();
        //String taskString = serializeUtil.serializeToString(datum.url());
        log.info("任务入口注入 ： " + datum.url());
        //注入任务到入口库
        String seeds = redisDb.getCrawlDB();
        log.info("入口数据库 :" + seeds);
        redisTemplate.opsForList().rightPush(seeds, datum.url());
    }

    @Override
    public void inject(CrawlDatums datums, boolean force) throws Exception {
        for (CrawlDatum x : datums) {
            inject(x, force);
        }
    }

    /**
     * desc: 合并入口数据库
     **/
    @Override
    public void merge() throws Exception {
        String seeds = redisDb.getCrawlDB();
        String parse = redisDb.getFetchDB();
        while (redisTemplate.opsForList().size(seeds) > 0) {
            String seedStr = redisTemplate.opsForList().leftPop(seeds);
            redisTemplate.opsForList().rightPush(parse, seedStr);
        }
    }

    @Override
    public DataBase getDataBase() {
        return null;
    }

    @Override
    public void setDataBase(DataBase dataBase) {

    }

    /**
     * desc: 初始化写入数据库工具
     **/
    @Override
    public void initSegmentWriter() throws Exception {

    }

    /**
     * desc: 写入已完成抓取的任务 可写入特定的数据库
     **/
    @Override
    public void writeFetchSegment(CrawlDatum fetchDatum) throws Exception {
        //String taskString = serializeUtil.serializeToString(fetchDatum);
        // log.info(fetchDatum.url());
        redisTemplate.opsForList().rightPush(redisDb.getLinkDB(), fetchDatum.url());
    }

    /**
     * desc: 写入接下来的任务到任务数据库
     **/
    @Override
    public void writeParseSegment(CrawlDatums parseDatums) throws Exception {
        for (CrawlDatum task : parseDatums) {
            //String nextTask = serializeUtil.serializeToString(task);
            //log.info(task.url());
            redisTemplate.opsForList().rightPush(redisDb.getFetchDB(), task.url());
        }
    }

    /**
     * desc: 关闭写入工具
     **/
    @Override
    public void closeSegmentWriter() throws Exception {

    }

    /**
     * desc: 清空数据库
     **/
    public void flushdb() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            log.info("启动清空以前的任务数据库");
            return "ok";
        });
    }
}
