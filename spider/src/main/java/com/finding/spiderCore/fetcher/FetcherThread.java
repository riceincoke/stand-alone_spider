package com.finding.spiderCore.fetcher;

import com.finding.spiderCore.entities.CrawlDatum;
import com.finding.spiderCore.entities.CrawlDatums;
import com.finding.spiderCore.fetcher.IFetcherTools.Executor;
import com.finding.spiderCore.fetcher.IFetcherTools.NextFilter;
import com.finding.spiderCore.spiderConfig.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * desc:线程，从queue中取得任务，并使用任务执行器执行
 **/
public class FetcherThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(FetcherThread.class);
    /**
    * 组件从fetcher中获取
    */
    private Executor executor;
    private NextFilter nextFilter;
    private FetchQueue fetchQueue;
    private QueueFeeder queueFeeder;
    private Fetcher fetcher;
    private Configuration configuration;
    /**
     * desc: 初始化执行线程
     **/
    public FetcherThread(Fetcher fetcher) {
        this.fetcher = fetcher;
        this.configuration = fetcher.getConfig();
        this.fetchQueue = fetcher.getFetchQueue();
        this.queueFeeder = fetcher.getQueueFeeder();
        this.executor = fetcher.getExecutor();
        this.nextFilter = fetcher.getNextFilter();
    }

    @Override
    public void run() {
        fetcher.getStartedThreads().incrementAndGet();//启动线程增加
        fetcher.getActiveThreads().incrementAndGet();//活动线程增加
        FetchItem item = null;
        try {
            //判断调度器是否处于运行状态
            while (fetcher.isFetcherRuning()) {
                try {
                    //从queue中取出任务
                    item = fetchQueue.getFetchItem();
                    if (item == null) {
                        if (queueFeeder.isAlive() || fetchQueue.getSize() > 0) {
                            // 任务添加器活动中 或者 任务管道不为零，阻塞线程增加1。
                            fetcher.getSpinWaiting().incrementAndGet();
                            try {
                                Thread.sleep(500);
                            } catch (Exception ex) {
                                log.error("spinWaiting thread sleep exception");
                            }
                            //重新开启等待中的线程 去获取任务
                            fetcher.getSpinWaiting().decrementAndGet();
                            continue;
                        }else{
                            break;
                        }
                    }

                    fetcher.getLastRequestStart().set(System.currentTimeMillis());

                    CrawlDatum crawlDatum = item.getDatum();
                    //String url = crawlDatum.getUrl();
                    //Page page = getPage(crawlDatum);

                    //crawlDatum.incrRetry(page.getRetry());
//                        crawlDatum.setFetchTime(System.currentTimeMillis());
                    CrawlDatums next = new CrawlDatums();
                    try {
                        //调用函数执行分析当前页面，取出页面的urls 形成 next。
                        executor.execute(crawlDatum, next);
                        //过滤next
                        if (nextFilter != null) {
                            CrawlDatums filteredNext = new CrawlDatums();
                            for (int i = 0; i < next.size(); i++) {
                                CrawlDatum filterResult = nextFilter.filter(next.get(i), crawlDatum);
                                if (filterResult != null) {
                                    filteredNext.add(filterResult);
                                }
                            }
                            next = filteredNext;
                        }
                       // log.info("done: " + crawlDatum.briefInfo());
                        //当前页面标记为 已爬取
                        crawlDatum.setStatus(CrawlDatum.STATUS_DB_SUCCESS);
                    } catch (Exception ex) {
                        log.info("failed: " + crawlDatum.briefInfo(), ex);
                        crawlDatum.setStatus(CrawlDatum.STATUS_DB_FAILED);
                    }

                    crawlDatum.incrExecuteCount(1);
                    crawlDatum.setExecuteTime(System.currentTimeMillis());
                    try {
                        //写入当前任务到已抓取的任务库
                        fetcher.getAbstractDbManager().writeFetchSegment(crawlDatum);
                        if (crawlDatum.getStatus() == CrawlDatum.STATUS_DB_SUCCESS && !next.isEmpty()) {
                            //写入当前任务提取的符合条件的任务到 后续未抓取的任务库
                            fetcher.getAbstractDbManager().writeParseSegment(next);
                        }
                    } catch (Exception ex) {
                        log.info("Exception when updating db", ex);
                    }
                    //当前页面执行完毕，等待时间
                    long executeInterval = configuration.getExecuteInterval();
                    if (executeInterval > 0) {
                        try {
                            Thread.sleep(executeInterval);
                        } catch (Exception sleepEx) {
                        }
                    }

                } catch (Exception ex) {
                    log.info("Exception", ex);
                }
            }

        } catch (Exception ex) {
            log.info("Exception", ex);

        } finally {
            //不管当前线程执行是否出错，活动线程都增加
            fetcher.getActiveThreads().decrementAndGet();
        }
    }
}
