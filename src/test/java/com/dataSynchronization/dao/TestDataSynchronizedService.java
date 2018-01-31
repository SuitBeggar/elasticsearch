package com.dataSynchronization.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.SearchApplication;
import com.dataSynchronization.service.DataSynchronizedService;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestDataSynchronizedService {

	@Autowired
	private DataSynchronizedService dataSynchronizedService;
	@Test
	public void test() {
		dataSynchronizedService.synchronizeData("caseindex_index");
		try {
			Thread.sleep(1000000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		List<Map<String, Object>> result = dataSynchronizedService.queryAllData("case-info", "2");
//		System.out.println(result);
	}

}
