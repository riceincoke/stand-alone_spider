package com.finding.myspider.redisSpider;

import com.finding.myspider.spiderTools.SerializeUtil;
import com.finding.spiderCore.crawldb.AbstractDBManager;
import com.finding.spiderCore.crawldb.Idbutil.DataBase;
import com.finding.spiderCore.entities.CrawlDatum;
import com.finding.spiderCore.entities.CrawlDatums;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * desc:连接操作redis类
 **/
@Component
public class RedisManager extends AbstractDBManager {
     private RedisDb redisDb;
     private RedisTemplate redisTemplate;
     private SerializeUtil serializeUtil;
    public RedisManager(RedisDb redisDb,RedisGenerator generator){
        super(generator);
        this.redisDb = redisDb;
        this.redisTemplate = generator.getRedisTemplate();
        this.serializeUtil = generator.getSerializeUtil();
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
 * @Return:
 **/
    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {
        String taskString = serializeUtil.serializeToString(datum);
        redisTemplate.opsForList().rightPush(redisDb.getFetchDB(), datum);
    }

    @Override
    public void inject(CrawlDatums datums, boolean force) throws Exception {
        for (CrawlDatum x:datums) {
            inject(x,force);
        }
    }
    /**
     * desc: 合并已抓取和未抓取的任务库
     * @Return: void
     **/
    @Override
    public void merge() throws Exception {

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
     * @Return: void
     **/
    @Override
    public void initSegmentWriter() throws Exception {

    }

    /**
     * desc: 写入已完成抓取的任务
     * @Return: void
     **/
    @Override
    public void writeFetchSegment(CrawlDatum fetchDatum) throws Exception {
        String taskString = serializeUtil.serializeToString(fetchDatum);
        redisTemplate.opsForList().rightPush(redisDb.getLinkDB(), taskString);
    }
    /**
     * desc: 写入接下来抓取的任务列表
     * @Return: void
     **/
    @Override
    public void writeParseSegment(CrawlDatums parseDatums) throws Exception {
        for (CrawlDatum task : parseDatums) {
            String nextTask = serializeUtil.serializeToString(task);
            redisTemplate.opsForList().rightPush(redisDb.getFetchDB(), nextTask);
        }
    }
    /**
     * desc: 关闭写入工具
     * @Return: void
     **/
    @Override
    public void closeSegmentWriter() throws Exception {

    }
}
