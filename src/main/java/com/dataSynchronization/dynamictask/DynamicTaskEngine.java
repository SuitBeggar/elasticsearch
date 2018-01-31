package com.dataSynchronization.dynamictask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.config.ConfigEngine;
import com.config.mapping.Application;
import com.config.mapping.IndexType;
import com.dataSynchronization.service.DataSynchronizedService;

import lombok.extern.slf4j.Slf4j;

/**
 * 动态线程池任务调度引擎
 * @author xubincheng 20180111
 *
 */
@Slf4j
@Component
public class DynamicTaskEngine {
	private final Logger logger = org.slf4j.LoggerFactory.getLogger(DynamicTaskEngine.class);
	/**线程池任务调度*/
	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	
	@Autowired
	private DataSynchronizedService dataSynchronizedService;
	
	@Autowired
	private ConfigEngine configEngine;
	
	private Map<String, ScheduledFuture<?>> scheduleMap = new HashMap<>();
	
	@Bean
    private ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(10);
		threadPoolTaskScheduler.setThreadNamePrefix("动态定时任务线程");
		return threadPoolTaskScheduler;
    }
	
	/**
	 * 创建indexName下配置的定时同步
	 * @param indexName
	 */
	public void createIncrementSyncTask(String indexName) {
		Application application = configEngine.getApplicationByIndexName(indexName);
		for (IndexType indexType : application.getIndextypelist()) {
			if (!indexType.getSyncswitch()) {
				continue;
			}
			String key = indexName + "-" + indexType.getName();
			if (scheduleMap.containsKey(key)) {
				// 停止任务，重新开启任务
				scheduleMap.get(key).cancel(true);
			}
			logger.info("创建增量数据同步任务，indexName=["+indexName+"]，indexType=["+indexType.getName()+"]，cronTrigger=["+indexType.getScheduled()+"]");
			ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler
					.schedule(getIncrementDataSyncTask(indexType), new CronTrigger(indexType.getScheduled()));
			scheduleMap.put(key, scheduledFuture);
		}
	}
	
	/**
	 * 获取增量任务
	 * @param indexType
	 * @return
	 */
	public Runnable getIncrementDataSyncTask(IndexType indexType) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				String threadName = "增量数据同步线程[indexName='"+indexType.getIndexName()+"',indexType='"+indexType.getName()+"']";
				Thread.currentThread().setName(threadName);
				dataSynchronizedService.syncIncrementData(indexType);
			}
		};
		return run;
	}
}
