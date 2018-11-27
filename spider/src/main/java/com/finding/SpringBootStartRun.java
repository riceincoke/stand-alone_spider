package com.finding;

import com.finding.myspider.SpiderEngine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;

@Configuration
public class SpringBootStartRun implements ApplicationRunner {
    private static final Logger log = Logger.getLogger(SpringBootStartRun.class);
    @Autowired
    private Environment environment;
    @Autowired
    private SpiderEngine spiderEngine;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void show() {
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        log.info("redisTemplate" +redisTemplate.toString());
        log.info(" environment properties : " +environment.getProperty("user.dir"));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //new TestSprider().testSpiderStart();
      //  demoTypeCrawler.startSpider();
        spiderEngine.start();
        //new RedisSpider(redisManager,true).startSpider();
    }
}
