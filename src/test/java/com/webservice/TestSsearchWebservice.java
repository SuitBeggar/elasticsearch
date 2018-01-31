package com.webservice;

import com.SearchApplication;
import ins.framework.exception.BusinessException;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestSsearchWebservice {

    @Test
    public void test(){
        JaxWsDynamicClientFactory dcf =JaxWsDynamicClientFactory.newInstance();

        Client client = dcf.createClient("http://192.168.20.83:9016/sinosoft/searchEngine?wsdl");
       // Object[] objects = new Object[0];


        HashMap<String,HashMap<String,List>> map=new HashMap<String,HashMap<String,List>>();
        HashMap<String,List> a = new HashMap<String,List>();
        List<String> lista = new ArrayList<String>();
        // lista.add("400020020172305102168");
        lista.add("公司");
        a.put("APPLINAME",lista);
        a.put("POLICYNO",lista);
        map.put("wildcard",a);


       // String indexName,String indexType,int from,int size,HashMap<String, HashMap<String, List >> map
        try {
            Object[] objects = client.invoke("SearchInterface","policyindex_index","policyindex_index",0,10,map);
            System.out.println("返回数据:" + objects[0].toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("调用接口失败",false);
        }

    }
}
