package com.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.config.mapping.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.config.datasource.DynamicDataSourceContextHolder;

import ins.framework.exception.BusinessException;

@Component
public class ConfigEngine {

	@Autowired
	private Applications applications;
	
	private Map<String, Application> applicationMap;

	private ElasticsearchCluster elasticsearchCluster;
	
	private Map<String, List<ChildSql>> childSqlMap;


	@PostConstruct
	public void init() {
		applicationMap = new HashMap<>();
		List<Application> applicationList = applications.getApplicationlist();
		if (applicationList!=null && !applicationList.isEmpty()) {
			for (Application application : applicationList) {
				String indexName = application.getIndexname();
				if (!applicationMap.containsKey(indexName)) {
					applicationMap.put(application.getIndexname(), application);
				} else {
					throw new BusinessException("配置异常：application的indexName["+indexName+"]重复，请检查", false);
				}
			}
		}


		elasticsearchCluster = applications.getElasticsearch();

		childSqlMap = new HashMap<>();
		if (applicationList!=null && !applicationList.isEmpty()) {
			for (Application application : applicationList) {
				String indexName = application.getIndexname();
				List<IndexType> indexTypeList = application.getIndextypelist();
				if (indexTypeList!=null && !indexTypeList.isEmpty()) {
					for (IndexType indexType : indexTypeList) {
						indexType.setIndexName(indexName);
						String name = indexType.getName();
						List<ChildSql> childSqlList = indexType.getChildsqlmap();
						if (childSqlList!=null && !childSqlList.isEmpty()) {
							Set<String> fkName = new HashSet<>();
							for (ChildSql childSql : childSqlList) {
								if (fkName.contains(childSql.getName())) {
									throw new BusinessException("配置异常：application的indexName["+indexName+"]的indexType["+name+"]的子表name["+childSql.getName()+"]重复，请检查", false);
								} else {
									fkName.add(childSql.getName());
								}
								String fk = childSql.getFk();
								if (StringUtils.isNotBlank(fk)) {
									String key = indexName + name + fk.toUpperCase();
									if (!childSqlMap.containsKey(key)) {
										List<ChildSql> childSqls = new ArrayList<>();
										childSqls.add(childSql);
										childSqlMap.put(key, childSqls);
									} else {
										childSqlMap.get(key).add(childSql);
									}
								} else {
									throw new BusinessException("配置异常：application的indexName["+indexName+"]的indexType["+name+"]的子表外键名为空，请检查", false);
								}
							}
							fkName = null;
						}
					}
				} else {
					throw new BusinessException("配置异常：application的indexName["+indexName+"]的indexType未配置，请检查", false);
				}
			}
		}
	}
	
	/**
	 * 获取所有配置的数据源
	 * @return
	 */
	public List<SycnDataSource> getAllSycnDataSource() {
		List<SycnDataSource> list = new ArrayList<>();
		for(Application application:applications.getApplicationlist()){
			list.add(application.getDatasource());
		}
		return list;
	}

	/**
	 * 根据IndexName获取Application
	 * @param indexName
	 * @return
	 */
	public Application getApplicationByIndexName(String indexName){
		if (applicationMap.containsKey(indexName)) {
			return applicationMap.get(indexName);
		}
		return null;
	}

	/**
	 * 根据IndexName获取数据源
	 * @param indexName
	 * @return
	 */
	public SycnDataSource getSycnDataSourceByIndexName(String indexName){
		if (applicationMap.containsKey(indexName)) {
			return applicationMap.get(indexName).getDatasource();
		}
		return null;
	}

	/**
	 * 根据indexName获取配置的同步sql信息
	 * @param indexName
	 * @return
	 */
	public List<IndexType> getIndexTypeByIndexName(String indexName) {
		if (applicationMap.containsKey(indexName)) {
			return applicationMap.get(indexName).getIndextypelist();
		}
		return null;
	}
	
	/**
	 * 根据IndexName和IndexType获取索引类型下的所有子表对象
	 * @param indexName
	 * @param indexType
	 * @return
	 */
	public boolean containsChildSqlByFK(String indexName, String indexType, String fk) {
		String key = indexName + indexType + fk.toUpperCase();
		return childSqlMap.containsKey(key);
	}
	
	public List<ChildSql> getChildSqlByFK(String indexName, String indexType, String fk) {
		String key = indexName + indexType + fk.toUpperCase();
		if (childSqlMap.containsKey(key)) {
			return childSqlMap.get(key);
		} 
		return null;
	}
	
	/**
	 * 根据数据源名称切换数据源
	 * @param dataSourceName
	 */
	public void setDataSource(String dataSourceName) {
		DynamicDataSourceContextHolder.setDataSource(dataSourceName);
	}
	
	/**
	 * 根据应用索引名称名称切换数据源
	 * @param indexName
	 */
	public void setDataSourceByIndexName(String indexName) {
		DynamicDataSourceContextHolder.setDataSource(getSycnDataSourceByIndexName(indexName).getName());
	}
	
	/**
	 * 返回当前数据源名称
	 * @return
	 */
	public String getCurrDataSource() {
		return DynamicDataSourceContextHolder.getDataSource();
	}


	/**
	 * 返回Elasticsearch集群信息
	 * @return
	 */
	public ElasticsearchCluster getElasticsearchCluster(){
		return elasticsearchCluster;
	}

	public String[] getHighFields(String indexName,String indexType){
		if(applicationMap.containsKey(indexName)){
			Application application = applicationMap.get(indexName);
			List<IndexType> indexTypeList = application.getIndextypelist();
			for (IndexType indextype : indexTypeList){
				if(indexType.equals(indextype.getName())){
						String [ ] highField = indextype.getHighfield();
						return highField;
				}
			}
		}
		return null;
	}
}
