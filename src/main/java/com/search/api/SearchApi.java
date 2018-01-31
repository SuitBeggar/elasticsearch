package com.search.api;

import com.search.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/searchData")
public class SearchApi {


    @Autowired
    private ElasticsearchService elasticsearchService;

    /**
     * 搜索API
     * @param request
     * @return
     */
    @RequestMapping(value = "/searchFromEsServer",method = RequestMethod.POST)
    public String  dataSearch(@RequestBody Map<String,Object> requestMap){
       // Map requestMap =   request.getParameterMap();
        HashMap<String,HashMap<String,List>> map = (HashMap<String, HashMap<String, List>>) requestMap.get("map");
        System.out.println(map.toString());
        String indexName = (String) requestMap.get("indexName");
        System.out.println(indexName);
        String indexType = (String) requestMap.get("indexType");
        System.out.println(indexType);
        System.out.println(requestMap.get("from"));
        System.out.println(requestMap.get("size"));
        int from = Integer.parseInt((String) requestMap.get("from"));
        int size = Integer.parseInt((String) requestMap.get("size"));
        String result =  elasticsearchService.queryResultAnalysis(indexName,indexType,from,size,map);
        return result;
    }
}
