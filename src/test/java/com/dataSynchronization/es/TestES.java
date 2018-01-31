package com.dataSynchronization.es;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.SearchApplication;
import com.dataSynchronization.DataSynchronizeEngine;
import com.mysql.fabric.xmlrpc.base.Array;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestES {

	@Autowired
	DataSynchronizeEngine dataSynchronizeEngine;
	@Test
	public void test(){
		List<String> list = new ArrayList();
		String jsonData="{\"handleruser\":\"0000000000\",\"taskname\":\"wf\",\"comcode\":\"00000000\",\"nodecode\":\"1\",\"subnodecode\":\"1\",\"workstatus\":\"1\",\"taskid\":20023002,\"registno\":\"410010020172301100336\",\"uppertaskid\":0,\"outofftime\":\"2017-06-29T11:37:17.000Z\",\"handlerstatus\":\"2\",\"riskcode\":\"123\",\"flowid\":\"410010020172301100336\",\"taskintime\":\"2017-06-29T11:37:16.000Z\"}";
		list.add(jsonData);
		Map<String,Object>  map = new HashMap<String,Object>();
		map.put("id","1");
		map.put("testtime","2015-07-13 00:00:00.0");
		System.out.println(map.toString());
		dataSynchronizeEngine.createIndex("test","test",map,"id");
;		//dataSynchronizeEngine.createIndexList("test", "test", list,"1");
	}
}
