package com.myspider.core.crawlColletor.webcollector.util;


import com.myspider.core.crawlColletor.webcollector.conf.Configured;

public class ConfigurationUtils {
//    public static void addParent(Object child, Configured parent){
//        if(child instanceof Configured){
//            Configured configuredChild = (Configured) child;
//            configuredChild.setParent(parent);
//        }
//    }

    public static void setTo(Configured from, Object... targets){
        for(Object target:targets){
            if(target instanceof Configured){
                Configured configuredTarget = (Configured) target;
                configuredTarget.setConf(from.getConf());
            }
        }
    }

}
