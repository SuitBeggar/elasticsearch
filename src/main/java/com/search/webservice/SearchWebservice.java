package com.search.webservice;

import com.search.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定义webservice搜索引擎接口
 *@author fangyitao
 *
 */
@WebService(targetNamespace = "http://webservice.search.com/")
public interface SearchWebservice {


    /**
     * 数据搜索
     * @param indexName
     * @param indexType
     * @param from
     * @param size
     * @param map
     * @return
     */
    @WebMethod
    String searchInterface(@WebParam(name = "indexName")String indexName,@WebParam(name = "indexType")String indexType,@WebParam(name = "from")int from,@WebParam(name = "size")int size,@WebParam(name = "map")HashMap<String,HashMap<String,List>> map);

    /**
     * 数据同步
     * @param indexName
     * @param indexType
     * @param dataList
     * @param idColumn
     */
    @WebMethod
    void dataSynchronizeInterface(@WebParam(name = "indexName")String indexName,@WebParam(name = "indexType")String indexType,@WebParam(name = "dataList")List<HashMap<String, Object>> dataList, @WebParam(name = "idColumn") String idColumn);
}
