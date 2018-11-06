package com.finding.myspider.redisSpider;

import com.finding.myspider.spiderComponent.MyRequester;
import com.finding.spiderCore.crawldb.DBManager;
import com.finding.spiderCore.crawler.AbstractSpider;
import com.finding.spiderCore.entities.CrawlDatums;
import com.finding.spiderCore.entities.Page;

public class RedisSpider extends AbstractSpider {

    public RedisSpider( ) {
        this.setAutoParse(true);
    }


    public void startSpider() throws Exception {
        this.addSeed("https://book.douban.com/tag/","taglist");
        this.setRequester(new MyRequester());
        /*可以设置每个线程visit的间隔，这里是毫秒*/
        //crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
        //crawler.setRetryInterval(1000);
        this.setThreads(30);
        this.start(3);
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
}
