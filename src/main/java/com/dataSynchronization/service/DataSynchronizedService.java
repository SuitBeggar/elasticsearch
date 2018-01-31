package com.dataSynchronization.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.config.mapping.AnalysField;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.config.ConfigEngine;
import com.config.mapping.ChildSql;
import com.config.mapping.IndexType;
import com.dataSynchronization.DataSynchronizeEngine;
import com.dataSynchronization.dao.DataSynchronizedDao;
import com.dataSynchronization.dynamictask.DynamicTaskEngine;

import ins.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataSynchronizedService {
	private final Logger logger = org.slf4j.LoggerFactory.getLogger(DataSynchronizedService.class);
	@Autowired
	private DataSynchronizedDao dataSynchronizedDao;
	
	@Autowired
	private ConfigEngine configEngine;
	
	@Autowired
	private DataSynchronizeEngine dataSynchronizeEngine;


	@Autowired
	private DynamicTaskEngine dynamicTaskEngine;
	
	/**
	 * 查询全量
	 * @param dynamicSql
	 * @return
	 */
	public List<Map<String, Object>> queryAllData(String dynamicSql) {
		logger.info("动态sql查询开始，sql > " + dynamicSql);
		Long start = System.currentTimeMillis();
		List<Map<String, Object>> result = dataSynchronizedDao.queryAllData(dynamicSql);
		logger.info("动态sql查询结束，耗时" + (System.currentTimeMillis() - start) + "ms");
		return result;
	}
	
	public void test() {
		configEngine.setDataSource("caseindex_index");
		List<String> list = dataSynchronizedDao.queryAllPrimaryKey("1");
		logger.info(JSON.toJSONString(list));
	}
	
	/**
	 * 同步应用数据
	 * @param indexName
	 */
	public void synchronizeData(String indexName) {
		// 切源
		configEngine.setDataSourceByIndexName(indexName);
		// 根据应用名indexName获取同步索引类型
		List<IndexType> indexTypeList = configEngine.getIndexTypeByIndexName(indexName);
		if (indexTypeList != null && !indexTypeList.isEmpty()) {
			for (IndexType indexType : indexTypeList) {
				//获取主表主键字段集合
				List<String> primaryKeyList = dataSynchronizedDao.queryAllPrimaryKey(indexType.getPrimarykeysql());
				if (primaryKeyList!=null && !primaryKeyList.isEmpty()) {
					//启动线程进行数据查询和索引生成
					multiThreadSyncData(indexName, indexType, primaryKeyList, indexType.getSyncthreadcount());
				}
			}
		} else {
			throw new BusinessException("应用[" + indexName + "]未配置同步索引类型indexType", false);
		}
		//启动增量同步定时
		//dynamicTaskEngine.createIncrementSyncTask(indexName);
	}
	
	/**
	 * 增量同步方法，传入indexType配置信息
	 * @param indexType
	 */
	public void syncIncrementData(IndexType indexType) {
		logger.info("启动增量数据同步");
		//切源
		configEngine.setDataSourceByIndexName(indexType.getIndexName());
		//String primaryKey = indexType.getPrimarykey();
		//String primaryTable = indexType.getPrimarytable();
		//String timeStampColumn = indexType.getTimestamp();
		//获取配置文件配置的时间戳字段和差异时间
		Long diffTime = Long.valueOf(indexType.getTimediffer());
		Date nowDate = new Date();
		Date oldDate = new Date(System.currentTimeMillis() - diffTime);
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
		String x = sdf.format(nowDate);
		String y = sdf.format(oldDate);
		String incrementsql = indexType.getIncrementsql().replace("${nowDate}","to_date('"+x+"','yyyy-mm-dd HH24:mi:ss')").replace("${oldDate}","to_date('"+y+"','yyyy-mm-dd HH24:mi:ss')");
		List<String> primaryKeyList = dataSynchronizedDao.queryIncrementPrimaryKey(incrementsql,nowDate,oldDate);
		if(primaryKeyList.size()>0){
			logger.info("增量同步数据 >>> " + JSON.toJSONString(primaryKeyList));
			multiThreadSyncData(indexType.getIndexName(), indexType, primaryKeyList, indexType.getSyncthreadcount());
		}

	}
	
	/**
	 * 多线程同步数据
	 * @param indexName	索引名称
	 * @param indexType	索引类型名称
	 * @param primaryKeyList 主键值列表
	 * @param threadCount 线程数
	 */
	public void multiThreadSyncData(final String indexName, final IndexType indexType, List<String> primaryKeyList, int threadCount) {
		if (primaryKeyList==null || primaryKeyList.isEmpty()) {
			return;
		}
		int dataSize = primaryKeyList.size();
		if (dataSize < threadCount) {
			threadCount = dataSize;
		}
		int step = dataSize / threadCount;
		//创建映射
		List<AnalysField> analyzeFields = indexType.getAnalysfield();
		try {
			dataSynchronizeEngine.createMapping(dataSynchronizeEngine.getMapping(analyzeFields,indexType.getName()),indexName,indexType.getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("索引[" + indexName + "]；"+"索引类型["+indexType+"]的mapping创建失败",false);
		}
		for (int i=0; i<threadCount; i++) {
			int fromIndex = i * step;
			int toIndex = (i + 1) * step;
			if (i==threadCount-1 && toIndex < dataSize) {
				toIndex = dataSize;
			}
			final List<String> somePrimaryKey = primaryKeyList.subList(fromIndex, toIndex);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					logger.info(Thread.currentThread().getName() + "同步开始");
					//切源
					configEngine.setDataSourceByIndexName(indexName);
					for (String primaryKey : somePrimaryKey) {
						String dynamicSql = indexType.getPrimarysql().replace("${primarykey}",primaryKey);
						Map<String, Object> mainResult = dataSynchronizedDao.queryUniqueData(dynamicSql);
						// 判断是否有返回值，且有子表配置，有则查询子表示数据
						if (mainResult != null) {
							List<String> columns = new ArrayList<>(mainResult.keySet());
							for (String columnName : columns) {
								// 字段名存在配置为外键的情况
								if (configEngine.containsChildSqlByFK(indexName, indexType.getName(), columnName)) {
									List<ChildSql> childSqlList = configEngine.getChildSqlByFK(indexName,
											indexType.getName(), columnName);
									for (ChildSql childSql : childSqlList) {
										String childSqlStr = childSql.getSql();
										if (childSqlStr.indexOf(" where ") > -1) {
											childSqlStr += " and " + columnName + "='" + (String) mainResult.get(columnName)
													+ "'";
										} else {
											childSqlStr += " where " + columnName + "='"
													+ (String) mainResult.get(columnName) + "'";
										}
										List<Map<String, Object>> childResultList = dataSynchronizedDao.queryAllData(childSqlStr);
										mainResult.put(childSql.getName(), childResultList);
									}
								}
							}
						}
						logger.info("创建[indexName='"+indexName+"', indexType='"+indexType.getName()+"']的索引");
						dataSynchronizeEngine.createIndex(indexName, indexType.getName(), mainResult,indexType.getPrimarykey());
					}
					logger.info(Thread.currentThread().getName() + "同步结束");
				}
			});
			thread.setName("数据同步线程 [indexName='"+indexName+"', indexType='"+indexType.getName()+"'] - No."+i);
			thread.start();
		}
	}
}
