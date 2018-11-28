package com.finding.myspider.DbUtils;

import com.finding.myspider.dao.SitesConfigDao;
import com.finding.myspider.entity.SiteConfig;
import com.finding.myspider.spiderTools.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${MysqlToRedis} </p>
 * <p>描述: [读取mysql 网站配置信息到redis队列] </p>
 **/
@Component
public class ConfigFromMysqlToRedis {

    @Autowired private RedisTemplate redisTemplate;
    @Autowired private SitesConfigDao sitesConfigDao;
    @Autowired private SerializeUtil serializeUtil;

    public void MysqlWirteRedis() {
        /**
         * desc:主节点需要该功能从mysql数据库读到redis队列
         **/
        List<SiteConfig> siteConfigObject = sitesConfigDao.Read();
        String oStr;
        for (SiteConfig sString : siteConfigObject) {
            try {
                oStr = serializeUtil.serializeToString(sString);
                redisTemplate.opsForList().leftPush("sites", oStr);
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
