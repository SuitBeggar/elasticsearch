package com.search;

import com.SearchApplication;
import com.config.TestConfigure;
import com.dataSynchronization.DataSynchronizeEngine;
import com.search.service.ElasticsearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestMap {
    private Logger logger = LoggerFactory.getLogger(TestConfigure.class);
    @Autowired
    DataSynchronizeEngine dataSynchronizeEngine;

    @Autowired
    ElasticsearchService elasticsearchService;

    @Test
    public void test(){

      /*  HashMap<String,HashMap<String,List>> map=new HashMap<String,HashMap<String,List>>();

        HashMap<String,List> a = new HashMap<String,List>();
        List<String> lista = new ArrayList<String>();
       // lista.add("400020020172305102168");
        lista.add("公司");
        a.put("APPLINAME",lista);
        a.put("POLICYNO",lista);
        map.put("wildcard",a);*/

       /* HashMap<String,List> b = new HashMap<String,List>();
        List<String> listb = new ArrayList<String>();
        listb.add("*0020120177102100599*");
       // listb.add("*2000002*");
        b.put("PRPLCOMPENSATE.COMPENSATENO",listb);
        map.put("wildcard",b);*/

        HashMap<String,HashMap<String,List>> map=new HashMap<String,HashMap<String,List>>();

        HashMap<String,List> wildcard = new HashMap<String,List>();
        HashMap<String,List> term = new HashMap<String,List>();

        List<String> searchWord = new ArrayList<>();
        searchWord.add("有限公司");
        List<String> platefilterWords = new ArrayList<>();
        //platefilterWords.add("1");
        List<String>  riskfilterWords = new ArrayList<>();

        wildcard.put("PRPLCLAIM.CLAIMNO",searchWord);
        wildcard.put("POLICYNO",searchWord);
        wildcard.put("REGISTNO",searchWord);
        wildcard.put("INSUREDNAME",searchWord);
        wildcard.put("DAMAGENAME",searchWord);

        term.put("BUSINESSPLATE",platefilterWords);
        term.put("CLASSCODE",riskfilterWords);

        map.put("wildcard",wildcard);
       // map.put("term",term);
       //elasticsearchService.queryConditionsAnalysis(map);
        BoolQueryBuilder boolQueryBuilder = elasticsearchService.queryConditionsAnalysis(map);
        SearchResponse searchResponse =  dataSynchronizeEngine.search("caseindex_index","caseindex_index",0,10,boolQueryBuilder);
        String json = elasticsearchService.queryResultAnalysis("caseindex_index","caseindex_index",0,10,map);
        logger.info("结果集为："+json);
        System.out.println("测试："+searchResponse.getHits().getTotalHits());
     }


     public  void testRrange(){
         HashMap<String,HashMap<String,List>> map=new HashMap<String,HashMap<String,List>>();

         HashMap<String,List> rang = new HashMap<String,List>();

         List<String> searchWord = new ArrayList<>();
         searchWord.add("a公司");
        // searchWord.add("11");
         rang.put("APPLINAME",searchWord);
         map.put("wildcard",rang);
         BoolQueryBuilder boolQueryBuilder = elasticsearchService.queryConditionsAnalysis(map);
         String json = elasticsearchService.queryResultAnalysis("policyindex_index","policyindex_index",0,10,map);
         SearchResponse searchResponse =  dataSynchronizeEngine.search("policyindex_index","policyindex_index",0,10,boolQueryBuilder);
         logger.info("结果集为："+json);
         System.out.println("测试："+searchResponse.getHits().getTotalHits());

     }
}
