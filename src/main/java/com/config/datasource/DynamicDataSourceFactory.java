package com.config.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.config.ConfigEngine;
import com.config.mapping.SycnDataSource;

/**
 * 动态数据源工厂，生产和存放数据源
 * @author Administrator
 *
 */
@Component  
@Configuration
public class DynamicDataSourceFactory {
	@Autowired
	private ConfigEngine configEngine;
	/**
	 * 创建数据源
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public DataSource createDataSource(String driverClassName, String url, String username, String password) {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(driverClassName);
		dataSourceBuilder.url(url);
		dataSourceBuilder.username(username);
		dataSourceBuilder.password(password);
		return dataSourceBuilder.build();
	}
	
	@Bean  
    public DynamicRoutingDataSourceResolver dataSource() {  
        DynamicRoutingDataSourceResolver resolver = new DynamicRoutingDataSourceResolver();  
  
        Map<Object, Object> dataSources = new HashMap<Object,Object>();
        //初始化配置的同步数据源
        List<SycnDataSource> sycnDataSourceList = configEngine.getAllSycnDataSource();
        System.out.println("初始化配置文件 START");
        for (SycnDataSource sycnDataSource : sycnDataSourceList) {
        	DataSource dataSource = this.createDataSource(sycnDataSource.getDriverclassname(), sycnDataSource.getUrl(), sycnDataSource.getUsername(), sycnDataSource.getPassword());
        	dataSources.put(sycnDataSource.getName(), dataSource);  
        	System.out.println("初始化数据源["+sycnDataSource.getName()+"]完成");
        }
        System.out.println("初始化配置文件 END");
  
        resolver.setTargetDataSources(dataSources);  
        // default datasource  
//        resolver.setDefaultTargetDataSource(dataSources);  
  
        return resolver;  
    }
}
