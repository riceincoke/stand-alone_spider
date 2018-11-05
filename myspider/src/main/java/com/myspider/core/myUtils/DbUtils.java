/*
package com.myspider.core.myUtils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.myspider.core.entity.SiteConfig;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Properties;

*/
/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${DbUtils} </p>
 * <p>描述: [数据库连接池，数据库连接类] </p>
 * <p>创建时间: ${date} </p>
 * @author <a href="mail to: 1139835238@qq.com" rel="nofollow">whitenoise</a>
 * @version v1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 **//*

public class DbUtils {
    private static Logger logger = Logger.getLogger(DbUtils.class.getSimpleName());

    private static ComboPooledDataSource dataSources;
    private static JdbcTemplate jdbcTemplate;
    static {
        Properties properties = new Properties();
        try {
            properties.load(DbUtils.class.getClassLoader().getResourceAsStream("jdbc.properties"));
            if (properties == null){
                logger.error("未获取到数据库配置文件");
            }
            logger.info("数据库配置文件："+properties.toString());
            //c3p0数据库连接池
            dataSources = new ComboPooledDataSource();
            dataSources.setDriverClass(properties.getProperty("jdbc.driverClass"));
            dataSources.setJdbcUrl(properties.getProperty("jdbc.url"));
            dataSources.setUser(properties.getProperty("jdbc.user"));
            dataSources.setPassword(properties.getProperty("jdbc.password"));
            dataSources.setInitialPoolSize(Integer.parseInt(properties.getProperty("jdbc.initialPoolSize")));
            dataSources.setAcquireIncrement(Integer.parseInt(properties.getProperty("jdbc.acquireIncrement")));
            dataSources.setAutoCommitOnClose(Boolean.parseBoolean(properties.getProperty("jdbc.autoCommitOnClose")));
            dataSources.setMaxIdleTime(Integer.parseInt(properties.getProperty("jdbc.maxIdleTime")));
            dataSources.setMaxPoolSize(Integer.parseInt(properties.getProperty("jdbc.maxPoolSize")));
            dataSources.setMinPoolSize(Integer.parseInt(properties.getProperty("jdbc.minPoolSize")));
            jdbcTemplate = new JdbcTemplate(dataSources);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
/**
     * desc:初始化mysql建表
     **//*

    public static void CreatTable(SiteConfig site){
        try {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS "+site.getTableName()
                            +"(new_id int(11) NOT NULL AUTO_INCREMENT,"
                            + "title varchar(100)," +
                            "url varchar(200)," +
                            "content longtext," +
                            "time VARCHAR(50)," +
                            "media VARCHAR(20)," +
                            "author VARCHAR(20),"
                            + "PRIMARY KEY (new_id)"
                            + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;"
            );
            logger.warn("提示 : CreatTable - "+site.getTableName()+ " Success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
        }
    }

    public static JdbcTemplate getJdbcTemplate(){
        return jdbcTemplate;
    }
}
*/
