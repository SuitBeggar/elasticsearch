package com.dataSynchronization.dynamictask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dataSynchronization.service.DataSynchronizedService;

/**
 * 增量数据任务工厂
 * @author Administrator
 *
 */
@Component
public class IncrementDataTaskFactory {
	@Autowired
	public DataSynchronizedService dataSynchronizedService;
	
	/**
	 * 获取增量任务
	 * @param indexName
	 * @param indexType
	 * @return
	 */
	public Runnable getTask(String indexName, String indexType) {
		Runnable run = new Runnable() {
			@Override
			public void run() {

				//dataSynchronizedService.syncIncrementData(indexName, indexType);
			}
		};
		return run;
	}
}
