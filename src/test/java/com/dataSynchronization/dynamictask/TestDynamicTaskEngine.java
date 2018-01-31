package com.dataSynchronization.dynamictask;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.SearchApplication;
import com.dataSynchronization.service.DataSynchronizedService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestDynamicTaskEngine {
	
	
	@Autowired
	private DynamicTaskEngine dynamicTaskEngine;
	
	@Autowired
	private DataSynchronizedService dataSynchronizedService;

	@Test
	public void test() {
		dynamicTaskEngine.createIncrementSyncTask("caseindex_index");
		try {
			Thread.sleep(10000000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
