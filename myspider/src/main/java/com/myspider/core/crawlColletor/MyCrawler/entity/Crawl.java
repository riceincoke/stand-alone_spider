package com.myspider.core.crawlColletor.MyCrawler.entity;

public class Crawl {
    private Integer Cid;
    private Integer Uid;

    public Crawl(Integer cid, Integer uid) {
        Cid = cid;
        Uid = uid;
    }

    public Crawl() {
    }

    @Override
    public String toString() {
        return "Crawl{" +
                "Cid=" + Cid +
                ", Uid=" + Uid +
                '}';
    }

    public Integer getCid() {
        return Cid;
    }

    public void setCid(Integer cid) {
        Cid = cid;
    }

    public Integer getUid() {
        return Uid;
    }

    public void setUid(Integer uid) {
        Uid = uid;
    }

}
