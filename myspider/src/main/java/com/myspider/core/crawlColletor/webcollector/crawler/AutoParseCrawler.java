/*
 * Copyright (C) 2014 hu
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
package com.myspider.core.crawlColletor.webcollector.crawler;

import com.myspider.core.crawlColletor.webcollector.fetcher.Executor;
import com.myspider.core.crawlColletor.webcollector.fetcher.Visitor;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatums;
import com.myspider.core.crawlColletor.webcollector.model.Links;
import com.myspider.core.crawlColletor.webcollector.model.Page;
import com.myspider.core.crawlColletor.webcollector.net.HttpRequest;
import com.myspider.core.crawlColletor.webcollector.net.Requester;
import com.myspider.core.crawlColletor.webcollector.util.ConfigurationUtils;
import com.myspider.core.crawlColletor.webcollector.util.RegexRule;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hu
 */

public abstract class AutoParseCrawler extends  Crawler implements Executor, Visitor, Requester {

    public static final Logger LOG = LoggerFactory.getLogger(AutoParseCrawler.class);

    /**
     * 是否自动抽取符合正则的链接并加入后续任务
     */
    protected boolean autoParse = true;
    protected Visitor visitor;
    protected Requester requester;

    public AutoParseCrawler() {
        this.requester = this;//自身即为接口的实现对象，调用request方法
        this.visitor = this;//自身即为接口的实现对象，调用visit方法
        this.executor = this;//自身即为接口的实现对象，调用executor方法
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [自身实现 requester接口，调用自身方法获取网页]
     * @author <a href="mail to: 113985238@qq.com" rel="nofollow">whitenoise</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        return request.responsePage();
    }

    @Override
    protected void registerOtherConfigurations() {
        super.registerOtherConfigurations();
        ConfigurationUtils.setTo(this, requester);//当前对象配置，付给当前对象配置
        ConfigurationUtils.setTo(this, visitor);
    }


    /**
     * URL正则约束
     */
    protected RegexRule regexRule = new RegexRule();

    @Override
    public void execute(CrawlDatum datum, CrawlDatums next) throws Exception {
        Page page = requester.getResponse(datum);
        //用户自定义页面内容抽取 重写visit方法
        visitor.visit(page, next);
        //执行页面urls抽取
        if (autoParse && !regexRule.isEmpty()) {
            parseLink(page, next);
        }
        afterParse(page, next);
    }

    protected void afterParse(Page page, CrawlDatums next) {
        //单个页面正文抽取，任务urls抽取完成之后，执行该函数
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [获取网页中符合 规则的url]
     * @author <a href="mail to: 113985238@qq.com" rel="nofollow">whitenoise</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    protected void parseLink(Page page, CrawlDatums next) {
        String conteType = page.contentType();
        if (conteType != null && conteType.contains("text/html")) {
            Document doc = page.doc();
            if (doc != null) {
                //从页面中取出需要的url 形成接下来的任务
                Links links = new Links().addByRegex(doc, regexRule, getConf().getAutoDetectImg());
                next.add(links);
            }
        }
    }

    /**
     * 添加URL正则约束
     *
     * @param urlRegex URL正则约束
     */
    public void addRegex(String urlRegex) {
        regexRule.addRule(urlRegex);
    }

    /**
     * @return 返回是否自动抽取符合正则的链接并加入后续任务
     */
    public boolean isAutoParse() {
        return autoParse;
    }

    /**
     * 设置是否自动抽取符合正则的链接并加入后续任务
     *
     * @param autoParse 是否自动抽取符合正则的链接并加入后续任务
     */
    public void setAutoParse(boolean autoParse) {
        this.autoParse = autoParse;
    }

    /**
     * 获取正则规则
     *
     * @return 正则规则
     */
    public RegexRule getRegexRule() {
        return regexRule;
    }

    /**
     * 设置正则规则
     *
     * @param regexRule 正则规则
     */
    public void setRegexRule(RegexRule regexRule) {
        this.regexRule = regexRule;
    }

    /**
     * 获取Visitor
     *
     * @return Visitor
     */
    public Visitor getVisitor() {
        return visitor;
    }

    /**
     * 设置Visitor
     *
     * @param visitor Visitor
     */
    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Requester getRequester() {
        return requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }


}
