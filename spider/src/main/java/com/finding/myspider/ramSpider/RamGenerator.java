package com.finding.myspider.ramSpider;

import com.finding.spiderCore.crawldb.AbstractGenerator;
import com.finding.spiderCore.crawldb.Idbutil.GeneratorFilter;
import com.finding.spiderCore.entities.CrawlDatum;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
@Component
public class RamGenerator extends AbstractGenerator<HashMap> {
    private static Logger log = Logger.getLogger(RamGenerator.class);
    public RamGenerator(RamDB ramDB,GeneratorFilter generatorFilter) {
        this.dataBase = ramDB;
        this.filter = generatorFilter;
    }

    @Override
    public CrawlDatum nextWithoutFilter() throws Exception {
        //获取爬虫任务数据库
        HashMap crawl = dataBase.getCrawlDB();
        //获取遍历器
        Iterator<Map.Entry> iterator = crawl.entrySet().iterator();
        if(iterator.hasNext()){
            Object key = iterator.next().getKey();
            CrawlDatum datum = (CrawlDatum) crawl.remove(key);
            return datum;
        }else{
            return null;
        }
    }

    @Override
    public void close() throws Exception {
    }
}
