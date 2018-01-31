package com.config.mybatis;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;

/**
 * mybatis配置扫描路径和数据源
 * @author xubincheng 20180111
 *
 */
@Configuration
@ConfigurationProperties(prefix = "mybatis")
@MapperScan(basePackages="com.dataSynchronization.dao",sqlSessionFactoryRef="sqlSessionFactory") // 扫面下面的mapper类
public class MabatisConfig {
private Resource[] mapperLocations;
	
	/**
	 * @return the mapperLocations
	 */
	public Resource[] getMapperLocations() {
		return mapperLocations;
	}

	/**
	 * @param mapperLocations the mapperLocations to set
	 */
	public void setMapperLocations(Resource[] mapperLocations) {
		this.mapperLocations = mapperLocations;
	}

	@Autowired
	@Qualifier("dataSource")
	DataSource dataSource;
	
	@Primary
	@Bean("sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setMapperLocations(mapperLocations);
		return sessionFactory.getObject();
	}
}
