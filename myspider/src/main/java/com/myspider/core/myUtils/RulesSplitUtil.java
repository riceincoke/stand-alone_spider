package com.myspider.core.myUtils;

import org.springframework.stereotype.Component;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${RulesSpiltUtil} </p>
 * <p>描述: [字符串转化为字符串集合] </p>
 **/

public class RulesSplitUtil {
    /**
     * desc:分解规则
     **/
    public String[] splitRule(String rule) {
        String[] list = rule.split(",");
       /* for (String x:list) {
            System.out.println("规则："+x+",");
        }*/
        return list;
    }

    /**
     * desc:剪切字符串
     **/
    public String SubStr(String str){
        if (str.length() > 20){
            str = str.substring(0,19);
        }
        return str;
    }
}
