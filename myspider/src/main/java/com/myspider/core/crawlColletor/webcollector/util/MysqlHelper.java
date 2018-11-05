/*
 * Copyright (C) 2016 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *//*

package com.myspider.core.crawlColletor.webcollector.util;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

*/
/**
 *数据库连接
 * @author hu
 *//*

public class MysqlHelper {

    private DataSource dataSource;
    public JdbcTemplate template;

    private MysqlHelper() throws PropertyVetoException {
        dataSource = createDataSource();
        template = new JdbcTemplate(dataSource);
     }

     */
/**
      * @Title：${enclosing_method}
      * @Description: [创建c3p0 连接池]
      * @Param: ${tags}
      * @Return: ${return_type}
      * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
      * @CreateDate: ${date} ${time}</p>
      * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
      *//*

    public DataSource createDataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setUser("root");
        dataSource.setPassword("1234");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/date?useUnicode=true&characterEncoding=utf8");
        return dataSource;
    }

    public JdbcTemplate getTemplate() {
        return template;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
*/
