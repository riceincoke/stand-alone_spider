package com.myspider.core.dao;

import com.myspider.core.redis.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>描述: [redis 数据操作 ] </p>
 * <p>创建时间: ${date} </p>
 * @author <a href="mail to: 1139835238@qq.com" rel="nofollow">whitenoise</a>
 **/
@Component
public class RedisDao {
    @Autowired RedisTemplate redisTemplate;//redis操作工具
    @Autowired SerializeUtil serializeUtil;//redis序列化工具
    /**
     * @Title：${enclosing_method}
     * @Description: [redis 插入序列化的对象]
     */
    public boolean insertAObject(String keyString,Object obj) {
        String objStr = null;
        try {
            objStr = serializeUtil.serializeToString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long reindex = redisTemplate.opsForSet().add(keyString,objStr);
        //Long reindex = jedis.sadd(keyString,objStr);
        if (reindex != 0){
            return true;
        }
        return false;
    }

}
