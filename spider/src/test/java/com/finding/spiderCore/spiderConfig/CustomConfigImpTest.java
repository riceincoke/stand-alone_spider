package com.finding.spiderCore.spiderConfig;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomConfigImpTest {
    private static final Logger LOG = Logger.getLogger(CustomConfigImpTest.class);
    @Test
    public void getDefault() {
        DefaultConfigImp configOpsImp = new DefaultConfigImp();
        Configuration configuration = configOpsImp.getConfig();
        configuration.setExecuteInterval(1000);
        LOG.info(configuration.toString());
    }

    @Test
    public void setDefault() {
    }
}