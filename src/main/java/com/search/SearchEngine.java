package com.search;

import com.search.service.ElasticsearchService;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchEngine {

    @Autowired
    private ElasticsearchService elasticsearchService;

    /**
     * 搜索
     * @param indexName
     * @param indexType
     * @param map
     * @return
     */
    public String  dataSearch(String indexName,String indexType,int from,int size,HashMap<String,HashMap<String,List>> map){
        String result =  elasticsearchService.queryResultAnalysis(indexName,indexType,0,10,map);
        return result;
    }


}
