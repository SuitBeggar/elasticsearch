package com.search;

import com.SearchApplication;
import com.config.TestConfigure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestApi {

    private Logger logger = LoggerFactory.getLogger(TestConfigure.class);
    @Test
    public  void test(){

        HashMap<String,HashMap<String,List>> map=new HashMap<String,HashMap<String,List>>();
        HashMap<String,List> a = new HashMap<String,List>();
        List<String> lista = new ArrayList<String>();
        // lista.add("400020020172305102168");
        lista.add("公司");
        a.put("APPLINAME",lista);
        a.put("POLICYNO",lista);
        map.put("wildcard",a);

        Map requestMap = new HashMap();
        requestMap.put("map",map);
        requestMap.put("indexName","policyindex_index");
        requestMap.put("indexType","policyindex_index");
        requestMap.put("from","0");
        requestMap.put("size","10");
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://192.168.20.83:9016/searchData/searchFromEsServer";
        String result = restTemplate.postForObject(url,requestMap,String.class);
        logger.info("结果集为："+result);
    }
}
