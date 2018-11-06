package com.finding.spiderCore.spiderConfig.ConfigUtil;


import com.finding.spiderCore.spiderConfig.CustomConfig;

public class ConfigurationUtils {
//    public static void addParent(Object child, Configured parent){
//        if(child instanceof Configured){
//            Configured configuredChild = (Configured) child;
//            configuredChild.setParent(parent);
//        }
//    }

    public static void setTo(CustomConfig from, Object... targets){
        for(Object target:targets){
            if(target instanceof CustomConfig){
                CustomConfig configuredTarget = (CustomConfig) target;
                configuredTarget.setConfig(from.getConfig());
            }
        }
    }

}
