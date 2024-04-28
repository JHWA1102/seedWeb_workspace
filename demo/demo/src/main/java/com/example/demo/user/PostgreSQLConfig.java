package com.example.demo.user;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostgreSQLConfig  {
	
	// implements ApplicationRunner

//	private final DataSource dataSource;
//	private final JdbcTemplate jdbcTemplate;
//	
//	public PostgreSQLConfig(DataSource dataSource, JdbcTemplate jdbcTemplate) {
//		this.dataSource = dataSource;
//		this.jdbcTemplate = jdbcTemplate;
//	}
//	
//	@Override
//	public void run(ApplicationArguments args) throws Exception {
//		// TODO Auto-generated method stub
//		try (Connection connection = dataSource.getConnection()) {
//			System.out.println("dataSource Class > " + dataSource.getClass());
//			System.out.println("URL > " + connection.getMetaData().getURL());
//			System.out.println("UserName > " + connection.getMetaData().getUserName());
//			
//			Statement statement = connection.createStatement();
//			String sql = "CREATE TABLE public.TBL_TEST(NO INTEGER NOT NULL, TEST_NAME VARCHAR(255), PRIMARY KEY (NO))";
//			statement.execute(sql);
//		} 
//		
//		jdbcTemplate.execute("INSERT INTO public.TBL_TEST VALUES (1, 'ABC')");
//		
//	}
	

}
