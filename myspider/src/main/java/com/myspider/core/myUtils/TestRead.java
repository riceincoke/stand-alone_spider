package com.myspider.core.myUtils;/*
package myUtils;

import entity.ContentRules;
import entity.SiteConfig;

import java.util.List;

public class TestRead {
    public static void main(String[] args){

        SitesRead sr = new SitesRead();

        */
/*SiteConfig sc = new SiteConfig();
        ContentRules cr = new ContentRules();
        cr.setTitle_rule("hd,title");
        cr.setContent_rule("Cnt-Main-Article-QQ,qq_article,infoTxt");
        cr.setTime_rule("article-time,a_time,time_source span");
        cr.setMedia_rule("a_source,time_source a,color-a-1");
        cr.setAnthor_rule("a_author,QQeditor");

        sc.setContentRules(cr);
        sc.setSiteName("大渝网");
        sc.setSiteUrl("http://cq.qq.com/");
        sc.setRes(true);
        sc.setAutoParse(true);
        sc.setSeeds("http://cq.qq.com/");
        sc.setUrlPares("http://cq.cqnews.net/html/.*,http://cq.cqnews.net/html/.*.htm,http://cq.qq.com/news/.*,http://cq.qq.com/CQxinwen/.*");
        sc.setContentPares("http://cq.qq.com/a/.*.htm,http://cq.qq.com/CQxinwen/.*,http://cq.qq.com/news/.*");
        sc.setDeepPath(2);
        sc.setTableName("dayuwang");

        System.out.println(sr.insertSiteConfig(sc));*//*

       List<SiteConfig> siteConfig = sr.Read();
        for (SiteConfig x:siteConfig) {
            System.out.println(x.toString());
        }
    }
}
*/
