/*
 * Copyright (C) 2015 hu
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
package com.finding.myspider.ramSpider;

import com.finding.myspider.spiderComponent.MyRequester;
import com.finding.spiderCore.crawler.AbstractAutoParseCrawler;
import com.finding.spiderCore.entities.CrawlDatums;
import com.finding.spiderCore.entities.Page;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * WebCollector 2.40新特性 page.matchType
 * 在添加CrawlDatum时（添加种子、或在抓取时向next中添加任务），
 * 可以为CrawlDatum设置type信息
 * 
 * type的本质也是meta信息，为CrawlDatum的附加信息
 * 在添加种子或向next中添加任务时，设置type信息可以简化爬虫的开发
 * 
 * 例如在处理列表页时，爬虫解析出内容页的链接，在将内容页链接作为后续任务
 * 将next中添加时，可设置其type信息为content（可自定义），在后续抓取中，
 * 通过page.matchType("content")就可判断正在解析的页面是否为内容页
 * 
 * 设置type的方法主要有3种：
 * 1）添加种子时，addSeed(url,type)
 * 2）向next中添加后续任务时：next.add(url,type)或next.add(links,type)
 * 3）在定义CrawlDatum时：crawlDatum.type(type)
 */
@Component
public class DemoTypeCrawler extends AbstractAutoParseCrawler {
    private static Logger log = Logger.getLogger(DemoTypeCrawler.class);
    @Autowired private RamDBManager ramDBManager;
    public DemoTypeCrawler() {
    }

    public void startSpider(){
        /**
         * 请求发起工具
         */
        MyRequester myRequester = new MyRequester();
        this.setRequester(myRequester);
        this.abstractDbManager = ramDBManager;
        /**
         * siteConfig
         */
        this.getConfig().setExecuteInterval(2000);
        this.addSeed("https://book.douban.com/tag/","taglist");
        this.setAutoParse(true);
        /*可以设置每个线程visit的间隔，这里是毫秒*/
        //crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
        //crawler.setRetryInterval(1000);
        this.setThreads(30);
        try {
            this.start(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        if(page.matchType("taglist")){
            //如果是列表页，抽取内容页链接
            //将内容页链接的type设置为content，并添加到后续任务中
            next.add(page.links("table.tagCol td>a"),"booklist");
        }else if(page.matchType("booklist")){
            next.add(page.links("div.info>h2>a"),"content");
        }else if(page.matchType("content")){
            //处理内容页，抽取书名和豆瓣评分
            String title=page.select("h1>span").first().text();
            String score=page.select("strong.ll.rating_num").first().text();
            System.out.println("title:"+title+"\tscore:"+score);
        }
    }

    @Override
    public String toString() {
        return "DemoTypeCrawler{" +
                "autoParse=" + autoParse +
                ", visitor=" + visitor.getClass().getName() +
                ", requester=" + requester.getClass().getName() +
                ", regexRule=" + regexRule +
                ", resumable=" + resumable +
                ", threads=" + threads +
                ", seeds=" + seeds +
                ", forcedSeeds=" + forcedSeeds +
                ", executor=" + executor.getClass().getName() +
                ", abstractDbManager=" + abstractDbManager.toString() +
                ", configuration=" + configuration.toString() +
                '}';
    }
}
