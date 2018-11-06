package com.finding.spiderCore.crawldb;

import com.finding.spiderCore.entities.CrawlDatum;
import com.finding.spiderCore.spiderConfig.DefaultConfigImp;
import org.springframework.stereotype.Component;

@Component
public class StatusGeneratorFilter extends DefaultConfigImp implements GeneratorFilter {
    /**
     * desc: 判断url携带的元数据 中的状态 过滤已爬取的
     * @Return: CrawlDatumn
     **/
    @Override
    public CrawlDatum filter(CrawlDatum datum) {
        if(datum.getStatus() == CrawlDatum.STATUS_DB_SUCCESS){
            return null;
        }else{
            return datum;
        }
    }
}
