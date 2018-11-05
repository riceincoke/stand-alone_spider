package com.myspider.core.crawlColletor.webcollector.crawldb;

import com.myspider.core.crawlColletor.webcollector.conf.DefaultConfigured;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatums;
import com.myspider.core.crawlColletor.webcollector.util.ConfigurationUtils;


public abstract class DBManager extends DefaultConfigured implements Injector, SegmentWriter{

    public abstract boolean isDBExists();

    public abstract void clear() throws Exception;

    public abstract Generator createGenerator();

    public Generator createGenerator(GeneratorFilter generatorFilter){
        Generator generator = createGenerator();
        generator.setFilter(generatorFilter);
        generator.setTopN(getConf().getTopN());
        ConfigurationUtils.setTo(this, generator, generatorFilter);
        return generator;
    }

    public abstract void open() throws Exception;

    public abstract void close() throws Exception;

    public abstract void inject(CrawlDatum datum, boolean force) throws Exception;

    public abstract void inject(CrawlDatums datums, boolean force) throws Exception;

    public abstract void merge() throws Exception;

    @Override
    public void inject(CrawlDatum datum) throws Exception {
        inject(datum, false);
    }

//    public void inject(CrawlDatums datums, boolean force) throws Exception {
//        for (CrawlDatum datum : datums) {
//            inject(datum, force);
//        }
//    }

    public void inject(CrawlDatums datums) throws Exception {
        inject(datums, false);
    }

    public void inject(Iterable<String> links, boolean force) throws Exception {
        CrawlDatums datums = new CrawlDatums(links);
        inject(datums, force);
    }

    public void inject(Iterable<String> links) throws Exception {
        inject(links, false);
    }

    public void inject(String url, boolean force) throws Exception {
        CrawlDatum datum = new CrawlDatum(url);
        inject(datum, force);
    }

    public void inject(String url) throws Exception {
        CrawlDatum datum = new CrawlDatum(url);
        inject(datum);
    }

}
