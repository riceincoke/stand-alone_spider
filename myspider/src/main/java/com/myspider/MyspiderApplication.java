package com.myspider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootApplication
public class MyspiderApplication {
   @Autowired private DataSource dataSource;
   @Autowired private JdbcTemplate jdbcTemplate;
   @PostConstruct
   public void showInit() throws SQLException {
   	 System.out.println(dataSource.toString());
   	 System.out.println(jdbcTemplate.getDataSource().getConnection());
   }

	public static void main(String[] args) {
		SpringApplication.run(MyspiderApplication.class, args);
	}
}
