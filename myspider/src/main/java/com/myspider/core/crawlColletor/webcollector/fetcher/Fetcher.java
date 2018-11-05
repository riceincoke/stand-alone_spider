/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.myspider.core.crawlColletor.webcollector.fetcher;

import com.myspider.core.crawlColletor.webcollector.conf.CommonConfigured;
import com.myspider.core.crawlColletor.webcollector.crawldb.DBManager;
import com.myspider.core.crawlColletor.webcollector.crawldb.Generator;
import com.myspider.core.crawlColletor.webcollector.crawldb.GeneratorFilter;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 抓取器
 *
 * @author hu
 */
public class Fetcher extends CommonConfigured {

    public static final Logger LOG = LoggerFactory.getLogger(Fetcher.class);

    public DBManager dbManager;

    public Executor executor;
    public NextFilter nextFilter = null;

    private AtomicInteger activeThreads;
    private AtomicInteger startedThreads;
    private AtomicInteger spinWaiting;
    private AtomicLong lastRequestStart;
    private QueueFeeder feeder = null;
    private FetchQueue fetchQueue = null;


    /**
     *
     */
    public static final int FETCH_SUCCESS = 1;

    /**
     *
     */
    public static final int FETCH_FAILED = 2;
    private int threads = 50;
    //private boolean isContentStored = false;

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     *
     */
    public static class FetchItem {

        public CrawlDatum datum;

        public FetchItem(CrawlDatum datum) {
            this.datum = datum;
        }
    }

    public static class FetchQueue {

        public AtomicInteger totalSize = new AtomicInteger(0);

        public final List<FetchItem> queue = Collections.synchronizedList(new LinkedList<FetchItem>());

        public void clear() {
            queue.clear();
        }

        public int getSize() {
            return queue.size();
        }

        public synchronized void addFetchItem(FetchItem item) {
            if (item == null) {
                return;
            }
            queue.add(item);
            totalSize.incrementAndGet();
        }

        public synchronized FetchItem getFetchItem() {
            if (queue.isEmpty()) {
                return null;
            }
            return queue.remove(0);
        }


        public synchronized void dump() {
            for (int i = 0; i < queue.size(); i++) {
                FetchItem it = queue.get(i);
                LOG.info("  " + i + ". " + it.datum.url());
            }

        }

    }

    //向Queue中添加任务 线程
    public static class QueueFeeder extends Thread {

        public FetchQueue queue;

        public DBManager dbManager;
        public Generator generator = null;
        public GeneratorFilter generatorFilter = null;
        public int size;

        public QueueFeeder(FetchQueue queue, DBManager dbManager, GeneratorFilter generatorFilter, int size) {
            this.queue = queue;
            this.dbManager = dbManager;
            this.generatorFilter = generatorFilter;
            this.size = size;
        }

        public void stopFeeder() {
            running = false;
            while (this.isAlive()) {
                try {
                    Thread.sleep(1000);
                    LOG.info("stopping feeder......");
                } catch (InterruptedException ex) {
                }
            }
        }

        public void closeGenerator() throws Exception {
            if (generator != null) {
                generator.close();
                LOG.info("close generator:" + generator.getClass().getName());
            }
        }

        public volatile boolean running = true;

        @Override
        public void run() {

            generator = dbManager.createGenerator(generatorFilter);
            LOG.info("create generator:" + generator.getClass().getName());
            String generatorFilterClassName = (generatorFilter == null) ? "null" : generatorFilter.getClass().getName();
            LOG.info("use generatorFilter:" + generatorFilterClassName);

            boolean hasMore = true;
            running = true;
            while (hasMore && running) {
                //监听queue中数量，当queue中数量为1000时，线程等待，
                int feed = size - queue.getSize();
                if (feed <= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    continue;
                }
                //如果queue中小于1000，往queue中添加新任务
                while (feed > 0 && hasMore && running) {
                    //任务生成器 如果下一个任务为空，返回空。判断dbmananger中是否有后续任务
                    CrawlDatum datum = generator.next();
                    hasMore = (datum != null);

                    if (hasMore) {
                        queue.addFetchItem(new FetchItem(datum));
                        feed--;//一直填到queue为1000
                    }
                }
            }
        }
    }

    //从Queue中取出任务
    private class FetcherThread extends Thread {

        @Override
        public void run() {
            startedThreads.incrementAndGet();//启动线程增加
            activeThreads.incrementAndGet();//活动线程增加
            FetchItem item = null;
            try {
                while (running) {
                    try {
                        //从queue中取出任务
                        item = fetchQueue.getFetchItem();
                        if (item == null) { // 如果未取到任务
                            if (feeder.isAlive() || fetchQueue.getSize() > 0) {
                                spinWaiting.incrementAndGet();// 并且 任务添加器活动中 或者 任务管道不为零，阻塞线程增加1。
                                try {
                                    Thread.sleep(500);//等待0.5秒
                                } catch (Exception ex) {
                                }
                                spinWaiting.decrementAndGet();//阻塞线程减少1。
                                continue;//继续判断任务是否为空
                            } else {
                                return;
                            }
                        }

                        lastRequestStart.set(System.currentTimeMillis());

                        CrawlDatum crawlDatum = item.datum;
                        //String url = crawlDatum.getUrl();
                        //Page page = getPage(crawlDatum);

                        //crawlDatum.incrRetry(page.getRetry());
//                        crawlDatum.setFetchTime(System.currentTimeMillis());
                        CrawlDatums next = new CrawlDatums();
                        try {
                            //调用函数执行分析当前页面，取出页面的urls 形成 next。
                            executor.execute(crawlDatum, next);
                            //过滤urls
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
                            LOG.info("done: " + crawlDatum.briefInfo());
                            //当前页面标明 已爬取
                            crawlDatum.setStatus(CrawlDatum.STATUS_DB_SUCCESS);
                        } catch (Exception ex) {
                            LOG.info("failed: " + crawlDatum.briefInfo(), ex);
                            crawlDatum.setStatus(CrawlDatum.STATUS_DB_FAILED);
                        }

                        crawlDatum.incrExecuteCount(1);
                        crawlDatum.setExecuteTime(System.currentTimeMillis());
                        //写入当前页面
                        try {
                            //已抓取的url
                            dbManager.writeFetchSegment(crawlDatum);
                            if (crawlDatum.getStatus() == CrawlDatum.STATUS_DB_SUCCESS && !next.isEmpty()) {
                               //写入当前页面符合条件的urls到dbmanager 中
                                dbManager.writeParseSegment(next);
                            }
                        } catch (Exception ex) {
                            LOG.info("Exception when updating db", ex);
                        }
                        //当前页面执行完毕，等待时间
                        long executeInterval = getConf().getExecuteInterval();
                        if (executeInterval > 0) {
                            try {
                                Thread.sleep(executeInterval);
                            } catch (Exception sleepEx) {
                            }
                        }

                    } catch (Exception ex) {
                        LOG.info("Exception", ex);
                    }
                }

            } catch (Exception ex) {
                LOG.info("Exception", ex);

            } finally {
                //不管当前线程执行是否出错，活动线程都增加
                activeThreads.decrementAndGet();
            }

        }

    }

    /**
     * 抓取当前所有任务，会阻塞到爬取完成 开启 feeder 和 执行爬取线程。
     *
     * @throws IOException 异常
     */
    public int fetchAll(GeneratorFilter generatorFilter) throws Exception {
        if (executor == null) {
            LOG.info("Please Specify A Executor!");
            return 0;
        }

        //将初始化任务库和形成的后续任务 合并为一个任务库
        dbManager.merge();

        try {
            dbManager.initSegmentWriter();
            LOG.info("init segmentWriter:" + dbManager.getClass().getName());
            running = true;
            lastRequestStart = new AtomicLong(System.currentTimeMillis());

            activeThreads = new AtomicInteger(0);
            startedThreads = new AtomicInteger(0);
            spinWaiting = new AtomicInteger(0);
            fetchQueue = new FetchQueue();
            //开启从Dbmanager中抽取任务添加到fetchQueue中，generator作任务状态过滤，添加上限1000个
            feeder = new QueueFeeder(fetchQueue, dbManager, generatorFilter, 1000);
            feeder.start();

            //开启消费者线程 从queue中读取任务
            FetcherThread[] fetcherThreads = new FetcherThread[threads];
            for (int i = 0; i < threads; i++) {
                fetcherThreads[i] = new FetcherThread();
                fetcherThreads[i].start();
            }

            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                LOG.info("-activeThreads=" + activeThreads.get()
                        + ", spinWaiting=" + spinWaiting.get() + ", fetchQueue.size="
                        + fetchQueue.getSize());

                if (!feeder.isAlive() && fetchQueue.getSize() < 5) {
                    fetchQueue.dump();
                }

                if ((System.currentTimeMillis() - lastRequestStart.get()) > getConf().getThreadKiller()) {
                    LOG.info("Aborting with " + activeThreads + " hung threads.");
                    break;
                }

            } while (running && (startedThreads.get() != threads || activeThreads.get() > 0));
            running = false;
            long waitThreadEndStartTime = System.currentTimeMillis();
            if (activeThreads.get() > 0) {
                LOG.info("wait for activeThreads to end");
            }
            /*等待存活线程结束*/
            while (activeThreads.get() > 0) {
                LOG.info("-activeThreads=" + activeThreads.get());
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                }
                if (System.currentTimeMillis() - waitThreadEndStartTime > getConf().getWaitThreadEndTime()) {
                    LOG.info("kill threads");
                    for (int i = 0; i < fetcherThreads.length; i++) {
                        if (fetcherThreads[i].isAlive()) {
                            try {
                                fetcherThreads[i].stop();
                                LOG.info("kill thread " + i);
                            } catch (Exception ex) {
                                LOG.info("Exception", ex);
                            }
                        }
                    }
                    break;
                }
            }
            LOG.info("clear all activeThread");
            feeder.stopFeeder();
            fetchQueue.clear();
        } finally {
            if (feeder != null) {
                feeder.closeGenerator();
            }
            dbManager.closeSegmentWriter();
            LOG.info("close segmentWriter:" + dbManager.getClass().getName());
        }
        return feeder.generator.getTotalGenerate();
    }

    volatile boolean running;

    /**
     * 停止爬取
     */
    public void stop() {
        running = false;
    }

    /**
     * 返回爬虫的线程数
     *
     * @return 爬虫的线程数
     */
    public int getThreads() {
        return threads;
    }

    /**
     * 设置爬虫的线程数
     *
     * @param threads 爬虫的线程数
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    public DBManager getDBManager() {
        return dbManager;
    }

    public void setDBManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }


    public NextFilter getNextFilter() {
        return nextFilter;
    }

    public void setNextFilter(NextFilter nextFilter) {
        this.nextFilter = nextFilter;
    }


}
