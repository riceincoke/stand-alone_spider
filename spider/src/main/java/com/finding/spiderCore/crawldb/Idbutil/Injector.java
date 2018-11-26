package com.finding.spiderCore.crawldb.Idbutil;

import com.finding.spiderCore.entities.CrawlDatum;

/**
 *入口注入
 */
public interface Injector {
      void inject(CrawlDatum datum) throws Exception;
}
