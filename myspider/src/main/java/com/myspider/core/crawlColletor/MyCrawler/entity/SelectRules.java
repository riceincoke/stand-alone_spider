package com.myspider.core.crawlColletor.MyCrawler.entity;

public class SelectRules {
    private Integer Srid;
    private Integer Uid;
    private String title_rule;
    private String content_rule;
    private String time_rule;
    private String media_rule;
    private String anthor_rule;

    @Override
    public String toString() {
        return "SelectRules{" +
                "Srid=" + Srid +
                ", Uid=" + Uid +
                ", title_rule='" + title_rule + '\'' +
                ", content_rule='" + content_rule + '\'' +
                ", time_rule='" + time_rule + '\'' +
                ", media_rule='" + media_rule + '\'' +
                ", anthor_rule='" + anthor_rule + '\'' +
                '}';
    }

    public SelectRules() {
    }

    public Integer getSrid() {
        return Srid;
    }

    public void setSrid(Integer srid) {
        Srid = srid;
    }

    public Integer getUid() {
        return Uid;
    }

    public void setUid(Integer uid) {
        Uid = uid;
    }

    public String getTitle_rule() {
        return title_rule;
    }

    public void setTitle_rule(String title_rule) {
        this.title_rule = title_rule;
    }

    public String getContent_rule() {
        return content_rule;
    }

    public void setContent_rule(String content_rule) {
        this.content_rule = content_rule;
    }

    public String getTime_rule() {
        return time_rule;
    }

    public void setTime_rule(String time_rule) {
        this.time_rule = time_rule;
    }

    public String getMedia_rule() {
        return media_rule;
    }

    public void setMedia_rule(String media_rule) {
        this.media_rule = media_rule;
    }

    public String getAnthor_rule() {
        return anthor_rule;
    }

    public void setAnthor_rule(String anthor_rule) {
        this.anthor_rule = anthor_rule;
    }
}
