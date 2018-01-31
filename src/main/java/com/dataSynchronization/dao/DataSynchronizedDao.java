package com.dataSynchronization.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface DataSynchronizedDao {
	
	/**
	 * 查询全量主键值
	 * @param primaryKeysql
	 * @return
	 */
	public List<String> queryAllPrimaryKey(@Param("primaryKeysql")String primaryKeysql);
	
	/**
	 * 查询增量主键值
	 * @param
	 * @return
	 */
	public List<String> queryIncrementPrimaryKey(@Param("incrementsql")String incrementsql,@Param("nowDate")Date nowDate,@Param("oldDate")Date oldDate);
	
	/**
	 * 查询惟一结果集
	 * @param dynamicSql
	 * @return
	 */
	public Map<String, Object> queryUniqueData(@Param("dynamicSql")String dynamicSql);
	
	/**
	 * 查询全量数据
	 * @param dynamicSql
	 * @return
	 */
	public List<Map<String, Object>> queryAllData(String dynamicSql);
}
