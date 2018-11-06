package com.finding.spiderCore.http;


import com.finding.spiderCore.entities.CrawlDatum;
import com.finding.spiderCore.entities.Page;

/**
 *发送请求接口
 */
public interface Requester {
     Page getResponse(CrawlDatum crawlDatum) throws Exception;
}
