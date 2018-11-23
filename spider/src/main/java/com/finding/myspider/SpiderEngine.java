package com.finding.myspider;


import com.finding.myspider.DbUtils.DataStoreTool;
import com.finding.myspider.ramSpider.RamDBManager;
import com.finding.myspider.redisSpider.RedisManager;
import com.finding.myspider.spiderComponent.MyRequester;
import com.finding.myspider.spiderComponent.MyParesContent;
import com.finding.myspider.spiderTools.ParesUtil;
import com.finding.myspider.entity.SiteConfig;
import com.finding.myspider.DbUtils.ConfigFromMysqlToRedis;
import com.finding.myspider.spiderTools.SerializeUtil;
import com.finding.spiderCore.spiderConfig.configUtil.ConfigurationUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SpiderEngine {
    private static final Logger log = Logger.getLogger(SpiderEngine.class.getSimpleName());
    /**
     *  数据管理组件
     **/
     @Autowired private RedisManager redisManager;
    //@Autowired private RamDBManager ramDBManager;
    @Autowired private RedisTemplate redisTemplate;
    @Autowired private ConfigFromMysqlToRedis configFromMysqlToRedis;
    @Autowired private SerializeUtil serializeUtil;
    /**
     *  爬虫组件
     **/
    @Autowired private ParesUtil paresUtil;
    @Autowired private MyRequester myRequester;
    @Autowired private DataStoreTool dataStoreTool;
    @Autowired private MyParesContent paresContent;

    @Override
    public String toString() {
        return "\nMySpiderEngine{" +
                "\n  ramDBManager : " + redisManager.getClass().getName() +
                "\n  redisTemplate : " + redisTemplate.getClass().getName() +
                "\n  siteConfigCache : " + configFromMysqlToRedis.getClass().getName() +
                "\n  myRequester : " + myRequester.getClass().getName() +
                "\n  paresContent=" + paresContent.toString() +
                '}';
    }

    public void start(){
        configFromMysqlToRedis.MysqlWirteRedis();
        //读取redis队列任务，并开始抓取
        //阻塞直到能取出值
        String objstr = "";
        try {
            while (true) {
                objstr = (String) redisTemplate.opsForList().leftPop("sites");
                //objstr = js.lpop("sites");
                if ("".equals(objstr) || objstr == null) {
                    log.error("redis 列表为空");
                    Thread.sleep(2000);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("读取redis队列失败"+e.getCause());
        }
        if ("".equals(objstr)) {
            log.error("redis 中 siteConfig 为空");
        } else {
            try {
                //获取序列化的字符串 生成siteConfig对象
                Object o = serializeUtil.deserializeToObject(objstr);
                SiteConfig sc = (SiteConfig) o;
                log.info("【"+sc.getSiteName() + "】   爬虫装载中------------->>>");

                /**
                 * DataToDB 数据持久化工具 param(tableName)
                 * paresUtil 网页解析工具 param(siteConfig,dataToDB)
                 * visitor 网页解析器
                 * mySpider 爬虫组合对象
                 * dbmanager 数据库
                 */
                dataStoreTool.initStore(sc.getTableName());
                paresUtil.initParesUitl(sc, dataStoreTool);
                paresContent.initParesUitl(paresUtil,sc.getContentRules());
                MySpider mySpider = new MySpider();
                ConfigurationUtils.setTo(mySpider, redisManager.getAbstractGenerator());
                mySpider.setAbstractDbManager(redisManager);
                mySpider.initMySpider(sc,paresContent, myRequester, paresUtil);
                log.info(this.toString());

                /*Thread t = new Thread(() -> {
                    threadSleep(10);
                    mySpider.stop();
                });
                t.start();*/

                mySpider.startFetcher(mySpider);
            } catch (Exception e) {
               log.error("something Wrong : "+e.getCause());
            }
        }
    }

    /**
     * desc:线程休眠
     **/
    public void threadSleep(Integer second){
        try {
            log.warn("停止任务线程休眠"+second+"秒------------");
            Thread.sleep(second*1000);
            } catch (InterruptedException e) {
            log.error("线程休眠异常");
        }
    }
    /* 线程休眠 end */
}
