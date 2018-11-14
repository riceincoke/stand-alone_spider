package com.finding.spiderCore.spiderConfig;

import com.finding.spiderCore.spiderConfig.IConfig.CustomConfig;

public class CustomConfigImp implements CustomConfig {
    protected Configuration configuration;
    @Override
    public Configuration getConfig() {
        return configuration;
    }
    @Override
    public void setConfig(Configuration configuration) {
        this.configuration = configuration;
    }
}
