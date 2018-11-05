package com.myspider.core;

import com.myspider.core.configs.JdbcConfig;
import com.myspider.core.crawlColletor.MyCrawler.entity.MyNew;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatum;
import com.myspider.core.crawlColletor.webcollector.model.CrawlDatums;
import com.myspider.core.crawlColletor.webcollector.model.Page;
import com.myspider.core.crawlColletor.webcollector.net.HttpRequest;
import com.myspider.core.crawlColletor.webcollector.net.Proxys;
import com.myspider.core.crawlColletor.webcollector.plugin.berkeley.BreadthCrawler;
import com.myspider.core.entity.ContentRules;
import com.myspider.core.entity.SiteConfig;
import com.myspider.core.myUtils.*;
import com.myspider.core.redis.SerializeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${MySpider} </p>
 * <p>描述: [爬虫初始化组件] </p>
 * <p>创建时间: ${date} </p>
 * @author <a href="mail to: 1139835238@qq.com" rel="nofollow">whitenoise</a>
 * @version v1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 **/
public class MySpider extends BreadthCrawler {

    private static Logger log = Logger.getLogger(MySpider.class.getSimpleName());
    //目标网站配置信息
    private SiteConfig siteconfig;
    //请求代理
     private Proxys proxys;
    //页面选择规则
    private ContentRules contentRules = new ContentRules();
    //数据库操作
    //时间过滤
    private TimeFilter timeFilter = new TimeFilter();
    //规则提取
    private RulesSplitUtil rulesSplitUtil = new RulesSplitUtil();
    //页面选择器
    private Selectors selectors = new Selectors();
    @Autowired private RedisTemplate redisTemplate;
    @Autowired private MysqlToRedis  mysqlToRedis;
    @Autowired private SerializeUtil serializeUtil;
    //总数
    private AtomicInteger TotalData = new AtomicInteger(0);
    //有效数
    private AtomicInteger Valid = new AtomicInteger(0);
    //无效
    private AtomicInteger Invalid = new AtomicInteger(0);
    //正文提取规则
    private String[] contRules;

    private static JdbcTemplate jdbcTemplate = JdbcConfig.getJdbcTemplate();
    /**
     * 构造一个基于伯克利DB的爬虫
     * 伯克利DB文件夹为crawlPath，crawlPath中维护了历史URL等信息
     * 不同任务不要使用相同的crawlPath
     * 两个使用相同crawlPath的爬虫并行爬取会产生错误
     */
    public MySpider() {

    }

    /**
     * @Title：${enclosing_method}
     * @Description: [初始化抓取任务]
     * @author <a href="mail to: 113985238@qq.com" rel="nofollow">whitenoise</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */

    public void init(SiteConfig siteConfig){
        this.siteconfig = siteConfig;
        this.setAutoParse(siteConfig.isAutoParse());
        this.setDbManager(siteConfig.getSiteName());
        this.autoParse = siteConfig.isAutoParse();
        this.contentRules = siteconfig.getContentRules();
        this.contRules = rulesSplitUtil.splitRule(siteConfig.getContentPares());
        //设置爬虫入口
        this.addMyRegx();
        //设置线程数
        this.setThreads(10);
        this.setMaxExecuteCount(5);
        System.out.println(jdbcTemplate);
    }
    /**
     * @Title：${enclosing_method}
     * @Description: [规则注入]
     * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void addMyRegx() {
        //抽取url提取规则
        String[] urlrules = rulesSplitUtil.splitRule(siteconfig.getUrlPares());
        String[] seeds = rulesSplitUtil.splitRule(siteconfig.getSeeds());
        //注入规则
        for (String str : seeds) {
            log.info("入口：" + str);
            this.addSeed(str, true);
        }

        for (String n : contRules) {
            log.info("正文提取规则注入:" + n);
            this.addRegex(n);
        }
        for (String s : urlrules) {
            log.info("url提取规则注入:" + s);
            this.addRegex(s);
        }
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [ip代理设置]
     * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
       /* Proxy proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress("14.18.16.67",80));
//        HashMap<String ,Integer> proxy= site.getProxys();
//        for (Map.Entry<String, Integer> entry : proxy.entrySet()) {
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue());
//            proxys.add(entry.getKey(),entry.getValue());
//        }
//        request.setProxy(proxys.nextRandom());
        request.setProxy(proxy);*/
        return request.responsePage();
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [页面正文提取]
     * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    @Override
    public void visit(Page page, CrawlDatums next) {
        TotalData.incrementAndGet();
        //匹配正文筛选规则 url
        for (String regx : contRules) {
            if (page.url().matches(regx)) {
                //获取标题
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
                        insert(myNew);
                        Valid.incrementAndGet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //正文为空
                    Invalid.incrementAndGet();
                    LOG.warn("已过滤正文为空的新闻");
                }
            }
        }
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [持久化数据到mysql数据库]
     * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
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

    /**
     * @Title：${enclosing_method}
     * @Description: [持久化数据到mysql数据库]
     * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void insert(MyNew myNew) {
        String sql = "insert into " + siteconfig.getTableName() + "(title,url,content,time,media,author) values(?,?,?,?,?,?)";
        //System.out.println(sql);
        //title url content time media author
        System.out.println(jdbcTemplate.toString());
        System.out.println(myNew.toString());
        int x = jdbcTemplate.update(sql, myNew.getTitle(), myNew.getURL(), myNew.getContent(), myNew.getTime(), myNew.getMedia(), myNew.getAnthor());
        if (x != 0) {
            log.info("存入数据成功");
        } else {
            log.info("存入数据失败");
        }
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [开启抓取任务]
     * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void StartFetcher(MySpider spider) {
        try {
            //设置断点爬取
            spider.setResumable(spider.siteconfig.isRes());
            spider.start(siteconfig.getDeepPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [任务完成后执行]
     * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    @Override
    public void afterStop() {
        log.info("本次处理的总数：" + TotalData);
        log.info("本次有效数据：" + Valid);
        log.info("本次无效数据：" + Invalid);
        log.info("等待10秒 继续下一任务");
        try {
            Thread.sleep(10000);
            //从mysql 中加载配置到redis中
            mysqlToRedis.MysqlWirteRedis();
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
            }
            Object o = serializeUtil.deserializeToObject(objstr);
            SiteConfig sc = (SiteConfig) o;
            log.info(sc.getSiteName() + " 装载中+++++");
            MySpider mySpider = new MySpider();
            mySpider.init(sc);
            //this.StartFetcher(this);
            mySpider.StartFetcher(mySpider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
