package com.myspider.core.crawlColletor.MyCrawler.entity;

import java.util.HashMap;
import java.util.List;

//目标网站
public class Site {
    private Integer Uid;
    private String SiteName;//网站名字
    private String SiteUrl;//urls
    private boolean Res;//断点
    private List<String> needs;//提取页面规则
    private List<String> seeds;//任务入口
    private Integer deepPath;//z抓取深度
    private boolean autoParse;//自动抓取
    private String tableName;//数据表名称
    private HashMap<String, Integer> proxys = new HashMap<String, Integer>();//代理ip


    public Site(Integer uid, String siteName, String siteUrl, boolean res, List<String> needs, List<String> seeds, Integer deepPath, boolean autoParse, String tableName, HashMap<String, Integer> proxys) {
        this.Uid = uid;
        this.SiteName = siteName;
        this.SiteUrl = siteUrl;
        this.Res = res;
        this.needs = needs;
        this.seeds = seeds;
        this.deepPath = deepPath;
        this.autoParse = autoParse;
        this.tableName = tableName;
        this.proxys = proxys;
    }

    public Site() {

    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public String getSiteUrl() {
        return SiteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        SiteUrl = siteUrl;
    }

    public boolean isRes() {
        return Res;
    }

    public void setRes(boolean res) {
        Res = res;
    }

    public List<String> getNeeds() {
        return needs;
    }

    public void setNeeds(List<String> needs) {
        this.needs = needs;
    }

    public List<String> getseeds() {
        return seeds;
    }

    public void setseeds(List<String> seeds) {
        this.seeds = seeds;
    }

    public Integer getDeepPath() {
        return deepPath;
    }

    public void setDeepPath(Integer deepPath) {
        this.deepPath = deepPath;
    }

    public boolean isAutoParse() {
        return autoParse;
    }

    public void setAutoParse(boolean autoParse) {
        this.autoParse = autoParse;
    }

    public HashMap<String, Integer> getProxys() {
        return proxys;
    }

    public void setProxys(HashMap<String, Integer> proxys) {
        this.proxys = proxys;
    }


    public Integer getUid() {
        return Uid;
    }

    public void setUid(Integer uid) {
        Uid = uid;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "Site{" +
                "Uid=" + Uid +
                ", SiteName='" + SiteName + '\'' +
                ", SiteUrl='" + SiteUrl + '\'' +
                ", Res=" + Res +
                ", needs=" + needs +
                ", seeds=" + seeds +
                ", deepPath=" + deepPath +
                ", autoParse=" + autoParse +
                ", tableName='" + tableName + '\'' +
                ", proxys=" + proxys +
                '}';
    }
}
