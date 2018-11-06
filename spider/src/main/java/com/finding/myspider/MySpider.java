package com.finding.myspider;


import com.finding.spiderCore.crawler.AutoParseCrawler;
import com.finding.spiderCore.pares.ParesContent;
import com.finding.myspider.spiderTools.ParesUtil;
import com.finding.myspider.entity.SiteConfig;
import com.finding.myspider.spiderTools.RulesSplitUtil;
import com.finding.spiderCore.crawldb.DBManager;
import com.finding.spiderCore.entities.CrawlDatums;
import com.finding.spiderCore.entities.Page;
import com.finding.spiderCore.http.Requester;
import com.finding.spiderCore.crawler.AbstractSpider;
import org.apache.log4j.Logger;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${MySpider} </p>
 * <p>描述: [爬虫初始化组件] </p>
 *
 **/
public class MySpider extends AutoParseCrawler {

    private static Logger log = Logger.getLogger(MySpider.class);
    /**
     * desc:网站配置信息
     **/
    private SiteConfig siteconfig;
    private ParesUtil paresUtil;
    private ParesContent paresContent;
    /**
     * urlRules url 解析正则表达式
     * seeds 入口 url
     * conPickRules 正文提取正则表达式
     */
    private String[] urlRules;
    private String[] seeds;
    private String[] conPickRules;

    public MySpider() {
        //设置任务上限
        this.configuration.setTopN(200);
        //设置线程数
        this.setThreads(50);
    }
    /**
     * @Title：${enclosing_method}
     * @Description: [初始化爬虫组件]
     * @param  siteConfig 网站配置信息
     * @param  paresContent 自定义页面解析器
     * @param requester 自定义请求工具 需实现requestor接口
     */
    public void initMySpider(SiteConfig siteConfig, ParesContent paresContent, Requester requester, ParesUtil paresUtil){
        this.siteconfig = siteConfig;
        this.paresContent = paresContent;
        this.requester = requester;
        this.paresUtil = paresUtil;

        RulesSplitUtil rulesSplitUtil = paresUtil.getRulesSplitUtil();
        urlRules = rulesSplitUtil.splitRule(siteconfig.getUrlPares());
        seeds = rulesSplitUtil.splitRule(siteconfig.getSeeds());
        conPickRules = rulesSplitUtil.splitRule(siteconfig.getContentPares());
        configSpider(siteConfig);
    }
    /**
     * desc: 加载爬虫属性
     **/
    public void configSpider(SiteConfig siteConfig){
         //设置爬虫入口
         this.addMyRegx();

         //设置断点爬取
         this.setResumable(siteconfig.isRes());
         //设置自动解析url
         this.setAutoParse(siteConfig.isAutoParse());
    }
    /**
     * @Title：${enclosing_method}
     * @Description: [规则注入]
     */
    public void addMyRegx() {
        //注入规则
        for (String str : seeds) {
            log.info("入口：" + str);
            this.addSeed(str, true);
        }
        for (String n : conPickRules) {
            log.info("正文提取规则注入:" + n);
            this.addRegex(n);
        }
        for (String u : urlRules) {
            log.info("url提取规则注入:" + u);
            this.addRegex(u);
        }
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [开启抓取任务]
     */
    public void startFetcher(MySpider spider) {
        try {
            spider.start(siteconfig.getDeepPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [任务完成后执行]
     */
    @Override
    public void afterStop() {
        log.info(paresUtil.getParesCounter().toString());
        log.info("等待10秒 继续下一任务--------------------------");
        try {
            Thread.sleep(10000);
            //log.info("又开始抓取拉——————————————————");
            //this.startFetcher(this);
            //从mysql 中加载配置到redis中
           /* mysqlToRedis.MysqlWirteRedis();
            String objstr;
            //阻塞直到能取出值
            while (true) {
                objstr = (String) redisTemplate.opsForList().leftPop("sites");
                //objstr = js.lpop("sites");
                if ("".equals(objstr) || objstr == null) {
                    log.error("redis 列表为空");
                    Thread.sleep(5000);
                } else {
                    break;
                }
            }*/
           // Object o = serializeUtil.deserializeToObject(objstr);
          //  SiteConfig sc = (SiteConfig) o;
           // log.info(sc.getSiteName() + " 装载中+++++");
            //MySpider mySpider = new MySpider();
          //  mySpider.init(sc);
            //this.StartFetcher(this);
            //mySpider.StartFetcher(mySpider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Page page, CrawlDatums next) {

        //匹配正文筛选规则 url
        for (String conRegx : conPickRules) {
            if (page.url().matches(conRegx)) {
                paresContent.paresContent(page,next);
            }
        }
    }
}
