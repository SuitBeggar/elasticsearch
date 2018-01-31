package com.search.service;

import com.alibaba.fastjson.JSON;
import com.config.ConfigEngine;
import com.dataSynchronization.DataSynchronizeEngine;
import com.dataSynchronization.service.DataSynchronizedService;
import com.search.constructor.ESQueryBuilderConstructor;
import ins.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据解析
 * @author fangyitao
 *
 */
@Slf4j
@Service
public class ElasticsearchService {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(DataSynchronizedService.class);

    @Autowired
    private ESQueryBuilderConstructor esQueryBuilderConstructor;

    @Autowired
    private DataSynchronizeEngine dataSynchronizeEngine;

    @Autowired
    private ConfigEngine configEngine;
    /**
     * 查询条件解析
     * @param map
     */
    public BoolQueryBuilder queryConditionsAnalysis(HashMap<String,HashMap<String,List>> map){
        logger.info ("Start the inquiry" );
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(map != null){
            //term查询解析
            if(map.containsKey("term")){
                HashMap<String,List> termMap = map.get("term");
                for(String key : termMap.keySet()) {
                        List<String> list = termMap.get(key);
                        if(list.size()>0){
                            QueryBuilder queryBuilder = esQueryBuilderConstructor.terms(key,termMap.get(key));
                            boolQueryBuilder.must(queryBuilder);
                        }
                }
            }
            //match查询解析
            if (map.containsKey("match")){
                HashMap<String,List> matchMap = map.get("match");
                for (String key : matchMap.keySet()){
                    List<String> list = matchMap.get(key);
                    if(list.size()>0){
                        QueryBuilder queryBuilder = esQueryBuilderConstructor.match(key,matchMap.get(key));
                        boolQueryBuilder.must(queryBuilder);
                    }
                }
            }
            //wildcard查询解析
            if(map.containsKey("wildcard")){
                //匹配中文、英文、数字
                String regexall = "^[a-z0-9A-Z\\u4e00-\\u9fa5]+$";
                //匹配全数字
                String regexNumber = "[0-9]*";
                //匹配英文和数字
                String regexNumberAndEnglish = "^[a-z0-9A-Z]+$";
                HashMap<String,List> wildcardMap = map.get("wildcard");
                BoolQueryBuilder wildcardBool = QueryBuilders.boolQuery();
                for (String key : wildcardMap.keySet()){
                    List<String> values = wildcardMap.get(key);
                    for(String value:values){
                        if(!StringUtils.isBlank(value)){
                            if(value.matches(regexNumber)||value.matches(regexNumberAndEnglish)){
                                WildcardQueryBuilder wildcardQueryBuilder = esQueryBuilderConstructor.wildcard(key,value);
                                wildcardBool.should(wildcardQueryBuilder);
                            }else{
                                QueryBuilder queryBuilder = esQueryBuilderConstructor.match(key,value);
                                wildcardBool.should(queryBuilder);
                            }
                        }
                    }
                }
                boolQueryBuilder.must(wildcardBool);
            }
            //prefix查询解析
            if(map.containsKey("prefix")){
                HashMap<String,List> prefixMap = map.get("prefix");
                for (String key : prefixMap.keySet()){
                    List<String> list = prefixMap.get(key);
                    if (list.size()>0){
                        QueryBuilder queryBuilder = esQueryBuilderConstructor.prefix(key,prefixMap.get(key));
                        boolQueryBuilder.must(queryBuilder);
                    }
                }
            }
            //range查询解析
            if (map.containsKey("range")){
                HashMap<String,List> rangeMap = map.get("range");
                for (String key : rangeMap.keySet()){
                    List<String> list = rangeMap.get(key);
                    if (list.size()>0){
                        QueryBuilder queryBuilder = esQueryBuilderConstructor.range(key,rangeMap.get(key));
                        boolQueryBuilder.must(queryBuilder);
                    }
                }
            }
        }
        return boolQueryBuilder;
    }


    /**
     * 查询结果集解析
     * @param indexName
     * @param indexType
     * @param from
     * @param size
     * @param map
     * @return
     */
    public String queryResultAnalysis(String indexName,String indexType,int from,int size,HashMap<String,HashMap<String,List>> map){
        QueryBuilder queryBuilder= this.queryConditionsAnalysis(map);
        String  result = "[";
        try{
            SearchResponse searchResponse = dataSynchronizeEngine.search(indexName,indexType,from,size,queryBuilder);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            if(searchHits.length>0){
                for (SearchHit hit : searchResponse.getHits()) {
                    Map<String, Object> source = hit.getSource();
                    //获取高亮信息
                    Map<String, HighlightField> highlightFields = hit.highlightFields();
                    String [ ] highFields = configEngine.getHighFields(indexName,indexType);
                    for (String highField : highFields){
                        HighlightField titleField = highlightFields.get(highField);
                        if(highlightFields.containsKey(highField)){
                            Text[] fragments = titleField.fragments();
                            if (fragments != null && fragments.length != 0){
                                StringBuilder name = new StringBuilder();
                                for (Text text : fragments) {
                                    name.append(text);
                                }
                                source.put(highField, name.toString());
                            }
                        }
                    }
                    String jsonData = JSON.toJSONString(source);
                    result = result+jsonData+",";
                }
                result = result.substring(0,result.length()-1);
            }
            result = result+"]";
        }catch (Exception e){
            throw new BusinessException("索引["+indexName+"]查询失败",false);
    }
        return result;
    }
}
