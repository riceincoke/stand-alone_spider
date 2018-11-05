package com.finding.spiderCore.fetcher;

import com.finding.spiderCore.crawldb.DBManager;
import com.finding.spiderCore.crawldb.Generator;
import com.finding.spiderCore.crawldb.GeneratorFilter;
import com.finding.spiderCore.crawldb.StatusGeneratorFilter;
import com.finding.spiderCore.entities.CrawlDatum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueueFeeder extends Thread {

    private static final Logger log = LoggerFactory.getLogger(QueueFeeder.class);

    private FetchQueue queue;
    private DBManager dbManager;
    private Generator generator;
    private FetchItem fetchItem;
    //private GeneratorFilter generatorFilter = null;
    private int queueMaxSize;// queeue 大小最大值设置
    public volatile boolean FeederRunning = true;//feeder 的状态

    /**
     * desc:初始化feeder
     *
     * @Return:
     **/
    public QueueFeeder(Fetcher fetcher, Integer size) {
        this.queue = fetcher.getFetchQueue();
        this.dbManager = fetcher.getDbManager();
        this.queueMaxSize = size;
    }

    /**
     * desc:关闭管道添加工具
     *
     * @Return: void
     **/
    public void stopFeeder() {
        //停止数据库提取工具
        try {
            closeGenerator();
        } catch (Exception e) {
            log.error("stoping generator exception");
        }
        FeederRunning = false;
        while (this.isAlive()) {
            try {
                Thread.sleep(1000);
                log.info("stopping feeder......");
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * desc:关闭任务生成工具
     *
     * @Return: void
     **/
    public void closeGenerator() throws Exception {
        if (generator != null) {
            generator.close();
            log.info("close generator:" + generator.getClass().getName() + " ......");
        }
    }


    /**
     * desc: feeder 线程运行
     *
     * @Return:
     **/
    @Override
    public void run() {
        //获取任务生成工具 （从数据库中提取数据）
        generator = dbManager.getGenerator();
        log.info(generator.toString());
        //generator.setFilter(new StatusGeneratorFilter());
        //log.info("create generator:" + generator.getClass().getName());
        boolean hasMore = true;//判断任务来源中是否存在任务 redis
        FeederRunning = true;
        while (hasMore && FeederRunning) {
            //监听queue中数量，当queue中数量为1000时，线程等待，
            int feed = queueMaxSize - queue.getSize();
            if (feed <= 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                continue;
            }
            //如果queue中小于1000，往queue中添加新任务
            while (feed > 0 && hasMore && FeederRunning) {
                //任务生成器 如果下一个任务为空，返回空。判断dbmananger中是否有后续任务
                CrawlDatum datum = generator.next();
                hasMore = (datum != null);
                if (hasMore) {
                    fetchItem = new FetchItem();
                    fetchItem.setDatum(datum);
                    queue.addFetchItem(fetchItem);
                    feed--;//一直填到queue为1000
                }
            }
        }
    }

    public Generator getGenerator() {
        return generator;
    }
}
