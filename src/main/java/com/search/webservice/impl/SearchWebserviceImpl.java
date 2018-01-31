package com.search.webservice.impl;

import com.dataSynchronization.DataSynchronizeEngine;
import com.search.service.ElasticsearchService;
import com.search.webservice.SearchWebservice;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService(targetNamespace = "http://webservice.search.com/",endpointInterface = "com.search.webservice.SearchWebservice")
public class SearchWebserviceImpl implements SearchWebservice{

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private DataSynchronizeEngine dataSynchronizeEngine;
    @Override
    public String searchInterface(String indexName,String indexType,int from,int size,HashMap<String, HashMap<String, List>> map) {
        String result = elasticsearchService.queryResultAnalysis(indexName,indexType,from,size,map);
        return result;
    }

    @Override
    public void dataSynchronizeInterface(String indexName, String indexType, List<HashMap<String, Object>> dataList, String idColumn) {
        dataSynchronizeEngine.createIndexByBulk(indexName,indexType,dataList,idColumn);
    }
}
