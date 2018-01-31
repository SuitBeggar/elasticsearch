package com.search.constructor;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static com.search.util.CommonUtil.isValidDate;


/**
 * 搜索条件封装
 * @author fangyitao
 *
 */
@Component
public class ESQueryBuilderConstructor {

    /**
     * 精确查询
     * @param key
     * @param values
     * @return
     */
    public QueryBuilder terms(String key,List<String> values){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for(String value : values){
            if(!StringUtils.isBlank(value)){
                QueryBuilder queryBuilder = QueryBuilders.termQuery(key,value);
                boolQueryBuilder.must(queryBuilder);
            }
        }
        return boolQueryBuilder;
    }
    /**
     * 通配符查询(一般用于字母和数字)
     * @param key
     * @param values
     * @return
     */
    public QueryBuilder wildcard(String key,List<String> values){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (String value : values) {
            if(!StringUtils.isBlank(value)){
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(key,"*"+value+"*");
                boolQueryBuilder.should(wildcardQueryBuilder);
            }
        }
        return boolQueryBuilder;
    }

    public WildcardQueryBuilder wildcard(String key,String  value){
        if(!StringUtils.isBlank(value)){
            WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(key,"*"+value+"*");
            return wildcardQueryBuilder;
        }
        return null;
    }
    /**
     * 分词查询（一般用于中文的查询）
     * @param key
     * @param values
     * @return
     */
    public QueryBuilder match(String key,List<String> values){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (String value :values){
            if(!StringUtils.isBlank(value)){
                QueryBuilder queryBuilder = QueryBuilders.matchQuery(key,value);
                boolQueryBuilder.should(queryBuilder);
            }
        }
        return boolQueryBuilder;
    }


    public QueryBuilder match(String key,String value){
        if(!StringUtils.isBlank(value)){
            QueryBuilder queryBuilder = QueryBuilders.matchQuery(key,value);
            return queryBuilder;
        }
        return null;
    }
    /**
     * 模糊查询（全字段）
     * @param value
     * @return
     */
   public QueryBuilder queryStringAll(String value){
       if(!StringUtils.isBlank(value)){
           QueryBuilder queryBuilde = QueryBuilders.queryStringQuery(value);
           return queryBuilde;
       }
       return null;
   }

    /**
     * 模糊查询（单字段，一般用于中文）
     * @param value
     * @return
     */
    public QueryBuilder queryString(String key,String value){
        if(!StringUtils.isBlank(value)){
            QueryBuilder queryBuilder = new QueryStringQueryBuilder(value).field(key);
            return queryBuilder;
        }
        return null;
    }
    /**
     * 前缀查询
     * @param key
     * @param values
     * @return
     */
   public QueryBuilder prefix(String key,List<String> values){
       BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
       for (String value : values){
           if(!StringUtils.isBlank(value)){
               QueryBuilder queryBuilder = QueryBuilders.prefixQuery(key,value);
               boolQueryBuilder.should(queryBuilder);
           }
       }
       return boolQueryBuilder;
   }

    /**
     * 区间查询
     * @param key
     * @param values
     * @return
     */
  public QueryBuilder range(String key,List<String> values){
      BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
      for (String value : values){
          if(!StringUtils.isBlank(value)){
              String[ ] valueString = value.split(",");
              if(isValidDate(valueString[0])&&isValidDate(valueString[1])){
                  QueryBuilder queryBuilde = QueryBuilders.rangeQuery(key).format("yyyy-MM-dd HH:mm:ss").gte(valueString[0]).lte(valueString[1]);
                  boolQueryBuilder.should(queryBuilde);
              }else {
                  QueryBuilder queryBuilder = QueryBuilders.rangeQuery(key).gte(valueString[0]).lte(valueString[1]);
                  boolQueryBuilder.should(queryBuilder);
              }
          }

      }
       return boolQueryBuilder;
   }
}
