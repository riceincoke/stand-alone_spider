package com.myspider.core;


import com.myspider.core.entity.SiteConfig;
import com.myspider.core.myUtils.MysqlToRedis;
import com.myspider.core.redis.SerializeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartSpider implements ApplicationRunner {

    private static final Logger log = Logger.getLogger(StartSpider.class.getSimpleName());

    @Autowired private RedisTemplate redisTemplate;
    @Autowired private MysqlToRedis mysqlToRedis;
    @Autowired private SerializeUtil serializeUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mysqlToRedis.MysqlWirteRedis();
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
        }catch (Exception e){
            log.error("读取redis队列失败");
            e.printStackTrace();
        }
        // String objStr = js.lpop("sites");
        if("".equals(objstr)){
            log.error("未能取出值");
        }else{
            try {
                //获取序列化的字符串 生成siteConfig对象
                Object o = serializeUtil.deserializeToObject(objstr);
                SiteConfig sc = (SiteConfig) o;
                log.info(sc.getSiteName() + " 装载中+++++");
                MySpider mySpider = new MySpider();
                mySpider.init(sc);
                mySpider.StartFetcher(mySpider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
