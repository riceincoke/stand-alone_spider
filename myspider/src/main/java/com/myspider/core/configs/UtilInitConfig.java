package com.myspider.core.configs;

import com.myspider.core.myUtils.RulesSplitUtil;
import com.myspider.core.myUtils.Selectors;
import com.myspider.core.myUtils.TimeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * desc:页面解析工具初始化
 * @Return:
 **/
@Configuration
public class UtilInitConfig {

    @Bean(name = "timeFilter")
    public TimeFilter getTimeFilter(){
        return new TimeFilter();
    }
    @Bean(name = "rulesSplitUtil")
    public RulesSplitUtil getRuleSplitUtil(){
        return new RulesSplitUtil();
    }
    @Bean(name = "selectors")
    public Selectors getSelectors(){
        return new Selectors();
    }
}
