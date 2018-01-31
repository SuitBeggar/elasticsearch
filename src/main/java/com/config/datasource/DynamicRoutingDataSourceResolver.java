package com.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Spring提供org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource来支持DataSource路由配置
 * 重写determineCurentLookupKey()方法，返回当前需要用到的数据源名称
 * @author xubincheng 20180111
 *
 */
public class DynamicRoutingDataSourceResolver extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return DynamicDataSourceContextHolder.getDataSource();
	}

}
