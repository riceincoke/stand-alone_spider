package com.myspider.core.myUtils;

import com.myspider.core.dao.SitesConfigDao;
import com.myspider.core.entity.SiteConfig;
import com.myspider.core.redis.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${MysqlToRedis} </p>
 * <p>描述: [读取mysql 网站配置信息到redis队列] </p>
 * <p>创建时间: ${date} </p>
 * @author <a href="mail to: 1139835238@qq.com" rel="nofollow">whitenoise</a>
 * @version v1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 **/
@Component
public class MysqlToRedis {

    @Autowired private RedisTemplate redisTemplate;
    @Autowired private SitesConfigDao sitesConfigDao;
    @Autowired private SerializeUtil serializeUtil;

    public void MysqlWirteRedis() {
        /**
         * desc:主节点需要该功能从mysql数据库读到redis队列
         **/
        List<SiteConfig> scs = sitesConfigDao.Read();
        String str;
        for (SiteConfig x : scs) {
            try {
                str = serializeUtil.serializeToString(x);
                redisTemplate.opsForList().leftPush("sites", str);
                //jedis.lpush("sites", str);
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
