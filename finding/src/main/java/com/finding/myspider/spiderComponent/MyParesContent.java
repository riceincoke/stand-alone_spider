package com.finding.myspider.spiderComponent;

import com.finding.myspider.DbUtils.dbInterfaces.Store;
import com.finding.myspider.entity.ContentRules;
import com.finding.myspider.entity.MyNew;
import com.finding.myspider.spiderTools.*;
import com.finding.spiderCore.entities.CrawlDatums;
import com.finding.spiderCore.entities.Page;
import com.finding.spiderCore.pares.ParesContent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author 一杯咖啡
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MyParesContent implements ParesContent {
    private static Logger log = Logger.getLogger(ParesUtil.class);
    private ContentRules contentRules;
    private ParesUtil paresUtil;
    private ParesCounter paresCounter;
    private Store dataStoreTool;
    private RulesSplitUtil rulesSplitUtil;
    private Selectors selectors;
    private TimeFilter timeFilter;

    public void initParesUitl(ParesUtil paresUtil, ContentRules contentRules) {
         this.paresUtil = paresUtil;
         paresCounter = paresUtil.getParesCounter();
         dataStoreTool =  paresUtil.getDataStoreTool();
         rulesSplitUtil = paresUtil.getRulesSplitUtil();
         selectors = paresUtil.getSelectors();
         timeFilter = paresUtil.getTimeFilter();
         this.contentRules = contentRules;
    }

    @Override
    public String toString() {
        return "MyVisitor{" +
                "\n paresUitl : " + paresUtil.toString() +
                '}';
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [页面正文提取]
     */
    @Override
    public void paresContent(Page page, CrawlDatums next) {
                //有效连接数+1
                paresCounter.getTotalData().incrementAndGet();
                String title = page.doc().title();
                if (title.trim().equals("")) {
                    title = selectors.IdClassSelect(page, rulesSplitUtil.splitRule(contentRules.getTitle_rule()));
                }
                //获取正文
                String content = selectors.detaliSelect(page, rulesSplitUtil.splitRule(contentRules.getContent_rule()));
                if (!content.trim().equals("")) {
                    //正文不为空 获取 作者，媒体，时间
                    String media = selectors.detaliSelect(page, rulesSplitUtil.splitRule(contentRules.getMedia_rule()));
                    String author = selectors.detaliSelect(page, rulesSplitUtil.splitRule(contentRules.getAnthor_rule()));
                    String time = selectors.detaliSelect(page, rulesSplitUtil.splitRule(contentRules.getTime_rule()));
                    //截取指定长度字符串
                    time = rulesSplitUtil.SubStr(timeFilter.getTimeByReg(time));
                    media = rulesSplitUtil.SubStr(media);
                    author = rulesSplitUtil.SubStr(author);
                    String url = page.url();
                    try {
                        MyNew myNew = GenerateNews(title, url, content, media, author, time);
                        dataStoreTool.insert(myNew);
                        paresCounter.getValid().incrementAndGet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //正文为空
                    paresCounter.getInvalid().incrementAndGet();
                    log.warn("已过滤正文为空的新闻");
                }
    }
    /**
     * @Title：${enclosing_method}
     * @Description: [生成MyNew对象]
     */
    public MyNew GenerateNews(String title, String url, String content, String media, String author, String time) {
        MyNew myNew = new MyNew();
        myNew.setTitle(title);
        myNew.setURL(url);
        myNew.setContent(content);
        myNew.setMedia(media);
        myNew.setAnthor(author);
        myNew.setTime(time);
        return myNew;
    }
}
