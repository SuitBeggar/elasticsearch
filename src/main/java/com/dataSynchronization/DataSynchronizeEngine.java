package com.dataSynchronization;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.config.ConfigEngine;
import com.config.mapping.AnalysField;
import com.dataSynchronization.service.DataSynchronizedService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import ins.framework.exception.BusinessException;

/**
 * 搜索引擎基本操作类
 * @author fangyitao
 *
 */
@Service
public class DataSynchronizeEngine {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(DataSynchronizedService.class);

	@Autowired
	private ConfigEngine configEngine;

	private TransportClient client;

	/**
	 * @author：fangyitiao
	 */
	@SuppressWarnings("resource")
	private TransportClient initClient(){
	    if(client == null || ((TransportClient) client).connectedNodes().isEmpty()){
	        synchronized (DataSynchronizeEngine.class){
                if (client == null || ((TransportClient) client).connectedNodes().isEmpty()) {
                    Settings settings = Settings.builder()
                            .put("client.transport.ping_timeout", "10s")
                            .put("cluster.name", configEngine.getElasticsearchCluster().getClustername())
                            // 主动嗅探整个集群的状态，注意：当ES服务器监听使用内网服务器IP而访问使用外网IP时，不要使用client.transport.sniff为true
                            .put("client.transport.ignore_cluster_name", false)
                            .put("client.transport.sniff", true).build();
                    try {
                    	String[] nodes = configEngine.getElasticsearchCluster().getClusternode();
                    	//集群节点加载
						client = new PreBuiltTransportClient(settings);
						for (String node :nodes){
							String[] hostPort = node.split(":");
							TransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(hostPort[0]),Integer.parseInt(hostPort[1]));
							client.addTransportAddress(transportAddress);
						}
                    } catch (java.net.UnknownHostException e) {
                        throw new BusinessException("初始化ES客户端错误",false);
                    }
                }
            }
        }
		return client;
	}

	/**
	 * 创建对应的映射
	 * @param analyzeFields
	 * @param indexType
	 * @return
	 */
	public XContentBuilder getMapping(List<AnalysField> analyzeFields, String indexType) throws Exception{
		XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject(indexType);
		List<String> formatList = new ArrayList<>();
		formatList.add("dateOptionalTime");
		formatList.add("yyyy/MM/dd HH:mm:ss Z||yyyy/MM/dd Z");
		formatList.add("yyyy-MM-dd HH:mm:ss.SSS");
		formatList.add("yyyy-MM-dd HH:mm:ss");
		formatList.add("yyyy-MM-dd");
		formatList.add("dd-MM-yyyy");
		formatList.add("yyyy/MM/dd");
		mapping = mapping.field("dynamic_date_formats",formatList);
		mapping = mapping.startObject("properties");
		for (AnalysField analysField : analyzeFields) {
			mapping = mapping.startObject(analysField.getField()).field("type","text").field("store","yes").field("analyzer","ik_smart").field("search_analyzer","ik_smart").endObject();
		}
		mapping = mapping.endObject().endObject().endObject();
		return mapping;
	}

	/**
	 * 创建映射
	 * @param mapping
	 * @param indexName
	 * @param indexType
	 * @return
	 * @throws Exception
	 */
	public PutMappingResponse createMapping(XContentBuilder mapping, String indexName, String indexType) {
		if(this.assertIndex(indexName,indexType)){

		}else {
			try {
				XContentBuilder builder = XContentFactory
						.jsonBuilder()
						.startObject()
						.field("number_of_shards", 5)
						.field("number_of_replicas", 1)
						.endObject();
				CreateIndexResponse response = initClient().admin().indices()
						.prepareCreate(indexName).setSettings(builder).execute().actionGet();
			}catch (Exception e){
				throw new BusinessException("创建失败",false);
			}
			}
		PutMappingResponse mappingResponse;
		try {
			PutMappingRequest mappingRequest = Requests.putMappingRequest(indexName).type(indexType).source(mapping);
			mappingResponse = initClient().admin().indices().putMapping(mappingRequest).actionGet();
		}catch (Exception e){
			e.printStackTrace();
			throw new BusinessException("索引[" + indexName + "]；"+"索引类型["+indexType+"]的mapping创建失败",false);
		}
		return mappingResponse;

	}


	/**
	 * 判断索引是否存在索引
	 *@author：fangyitiao
	 * */
	public boolean assertIndex(String indexName,String indexType) {
		IndicesExistsResponse  response = initClient().admin().indices().exists(new IndicesExistsRequest().indices(new String[]{indexName})).actionGet();
        return response.isExists();
	}


	/**
	 * 插入数据
	 * @author：fangyitiao
	 * @param indexName  索引名称，相当于数据库名称
	 * @param typeName 索引类型，相当于数据库中的表名
	 * @param idColumn id名称，相当于每个表中某一行记录的标识
	 *
	 */
	public void createIndexList(String indexName,String typeName, List<Map<String, Object>> dataList, String idColumn) {
			IndexRequestBuilder requestBuilder = initClient().prepareIndex(indexName, typeName);
			// 设置索引名称,索引类型,id
			if (dataList!=null && !dataList.isEmpty()) {
				for (Map<String, Object> data : dataList) {
					JSONObject jsonObject = JSONObject.fromObject(data);
					String id = jsonObject.getString(idColumn.toUpperCase());
					requestBuilder.setSource(data).setId(id).execute().actionGet();
				}
			}

	}
	
	public void createIndex(String indexName,String typeName,Map<String, Object> data,String idName) { 
		IndexRequestBuilder requestBuilder = initClient().prepareIndex(indexName, typeName);

		JSONObject jsonObject = JSONObject.fromObject(data);
		String id = jsonObject.getString(idName.toUpperCase());
        requestBuilder.setSource(data).setId(id).execute().actionGet();
	}

	/**
	 * 批量插入数据
	 * @param indexName
	 * @param typeName
	 * @param dataList
	 * @param idColumn
	 */
	public void createIndexByBulk(String indexName, String typeName, List<HashMap<String, Object>> dataList, String idColumn){
		BulkRequestBuilder bulkRequestBuilder = initClient().prepareBulk();
		int count=0;
		for (Map map : dataList){
			JSONObject jsonObject = JSONObject.fromObject(map);
			String id = jsonObject.getString(idColumn.toUpperCase());
			bulkRequestBuilder.add(initClient().prepareIndex(indexName,typeName).setSource(map).setId(id));
			//每100条插入一次
			if (count%100==0){
				bulkRequestBuilder.execute().actionGet();
			}
			count++;
		}
		bulkRequestBuilder.execute().actionGet();
	}
	/**
	 * 使用“_doc”全量查询
	 * @author：fangyitiao
	 */
	public SearchResponse searcher(String indexName, String type) {
		SearchResponse searchResponse = initClient().prepareSearch(indexName)  
                .setTypes(type)  
                .setQuery(QueryBuilders.matchAllQuery())  
                .addSort(SortBuilders.fieldSort("_doc"))  
                .setSize(30)  
                // 这个游标维持多长时间  
                .setScroll(TimeValue.timeValueMinutes(8)).execute().actionGet();
		return searchResponse;
	}
	
	/**
	 * 全量搜索(配合“_doc”使用)
	 * @author：fangyitiao
	 * @return
	 */
	public SearchResponse  scanData (String indexName, String typeName,String scrollId){  
        SearchResponse searchResponse = initClient().prepareSearchScroll(scrollId)  
                .setScroll(TimeValue.timeValueMinutes(8)).execute().actionGet();       
        return searchResponse;  
    } 
	
	/**
	 * 执行精确搜索
	 * @author：fangyitiao
	 * @param indexName 索引名称
	 * @param type 索引类型
	 * @param queryBuilder 查询条件
	 * @return
	 */
	public SearchResponse search(String indexName, String type,int from,int size,QueryBuilder queryBuilder) {

		HighlightBuilder hiBuilder=new HighlightBuilder().field("*").requireFieldMatch(false);
		hiBuilder.preTags("<span style=\"background:yellow\">");
		hiBuilder.postTags("</span>");
		ScoreSortBuilder scoreSortBuilders = SortBuilders.scoreSort().order(SortOrder.DESC);
		FieldSortBuilder sortBuilders = SortBuilders.fieldSort("REPORTTIME").order(SortOrder.DESC);
		logger.info(initClient().prepareSearch(indexName).setTypes(type).setQuery(queryBuilder).setFrom(from).setSize(size).highlighter(hiBuilder).setExplain(true).addSort(scoreSortBuilders).addSort(sortBuilders).toString());
		SearchResponse serachResponse = initClient().prepareSearch(indexName).setTypes(type).setQuery(queryBuilder).setFrom(from).setSize(size).highlighter(hiBuilder).setExplain(true).addSort(scoreSortBuilders).addSort(sortBuilders).execute().actionGet();
		return serachResponse;
	}
	
	/**
	 * 执行精确搜索(分页)
	 * @param indexName 索引名称
	 * @param type 索引类型
	 * @param pageNo 页码
	 * @param pageSize 每页显示数目
	 * @param queryBuilder 查询条件
	 * @return
	 */
	public SearchResponse searchPage(String indexName, String type,int pageNo,int pageSize,QueryBuilder queryBuilder,String field,SortOrder order) {
		SearchResponse serachResponse = initClient()
				.prepareSearch(indexName)
				.setTypes(type)
				.setQuery(queryBuilder)
				.addSort(field+".keyword", order)
				.setFrom((pageNo-1)*pageSize)
				.setSize(pageSize)
				.setExplain(true)
				.execute().actionGet();			
		return serachResponse;
	}
	/**
	 * 执行模糊搜索（分页）
	 * @param indexName 索引名称
	 * @param type 索引类型
	 * @param queryBuilder 查询条件
	 * @return
	 */
	public SearchResponse searchPage(String indexName, String type,int pageNo,int pageSize,QueryBuilder queryBuilder) {
		SearchResponse serachResponse = initClient()
				.prepareSearch(indexName)
				.setTypes(type)
				.setQuery(queryBuilder)
				.setFrom((pageNo-1)*pageSize)
				.setSize(pageSize)
				.execute().actionGet();
		return serachResponse;
	}	
	/**
	 * 执行模糊搜索（分页）
	 * @param indexName 索引名称
	 * @param type 索引类型
	 * @param queryBuilder 查询条件
	 * @return
	 */
	public SearchResponse searchPage(String indexName, String type,int pageNo,int pageSize,WildcardQueryBuilder queryBuilder) {
		SearchResponse serachResponse = initClient().prepareSearch(indexName).setTypes(type).setQuery(queryBuilder).setFrom((pageNo-1)*pageSize).setSize(pageSize).execute().actionGet();
		return serachResponse;
	}

    /**
     * 匹配查询
     * @param indexName
     * @param type
     * @param from
     * @param size
     * @param queryBuilder
     * @return
     */
	public SearchResponse search(String indexName, String type,int from,int size,WildcardQueryBuilder queryBuilder) {
		SearchResponse serachResponse = initClient().prepareSearch(indexName).setTypes(type).setFrom(from).setSize(size).setQuery(queryBuilder).execute().actionGet();		
		return serachResponse;
	}

	/**
	 * 更新索引
	 * @param indexName        索引名称
	 * @param typeName         索引类型
	 * @param idColumn         id名称
	 * @param jsonData         json数据
	 */
	public void updateIndex(String indexName,String typeName,List<String> jsonData,String idColumn) {
		if(jsonData.size()>0) {
			for(int i = 0;i<jsonData.size();i++) {
//				JSONObject jsonObject = JSONObject.fromObject(jsonData.get(i));
//				String id = jsonObject.getString(idColumn);
//				UpdateRequest updateRequest = new UpdateRequest();
//			    updateRequest.index(indexName);
//				updateRequest.type(typeName);
//				updateRequest.id(id);
//			    updateRequest.doc(jsonData);		
//				initClient().update(updateRequest).actionGet();
			}					
		}
		
	}

	/**
	 * 删除索引
	 * @param indexName
	 * @param typeName
	 * @param id
	 */
	
	public void deleteIndex(String indexName, String typeName, String id) {
		initClient().prepareDelete(indexName, typeName, id).get();
	}

    /**
     * 关闭客户端
     * @param client
     */
	public void close(Client client){
	    if (client != null){
	        client.close();
        }
    }
}
