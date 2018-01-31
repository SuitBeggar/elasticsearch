package com.search;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestMapping {

    @Test
    public void test(){
        Settings settings = Settings.builder()
                .put("client.transport.ping_timeout", "10s")
                .put("cluster.name", "my-application")
                // 主动嗅探整个集群的状态，注意：当ES服务器监听使用内网服务器IP而访问使用外网IP时，不要使用client.transport.sniff为true
                .put("client.transport.ignore_cluster_name", false)
                .put("client.transport.sniff", true).build();
        Client client = null;   //建立链接
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.20.83"), 9301));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        client.admin().indices().prepareCreate("producthuzhuindex").execute().actionGet();  //创建一个空索引，如没有索引，创建mapping时会报错
        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory.jsonBuilder().startObject() .startObject("producthuzhuindex").startObject("properties")
                    .startObject("plan_intro")  //嵌套对象字段
                    .startObject("properties")
                    .startObject("item").field("type", "text").field("store", "yes").field("analyzer", "ik_smart").field("search_analyzer", "ik_smart").endObject()
                    .startObject("content").field("type", "text").field("store", "yes").field("analyzer", "ik_smart").field("search_analyzer", "ik_smart").endObject()
                    .endObject()
                    .endObject()

                    .startObject("today_member").field("type", "text").field("store", "yes").endObject()   //普通字段



                    //多字段  ： 这个意思，我理解，就是一个字段有多个类型，如下这个，既有一个analyzer = id，又有一个no_analyzed   可以用于全文检索，还可以做精确查找。

                    .startObject("name").field("type", "text").field("store", "yes").field("analyzer","ik_smart")
                    .startObject("fields").startObject("unname").field("type", "text").field("index","not_analyzed").endObject().endObject()
                    .endObject()
                    .endObject().endObject().endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PutMappingRequest mappingRequest = Requests.putMappingRequest("producthuzhuindex").type("producthuzhuindex").source(mapping);
        client.admin().indices().putMapping(mappingRequest).actionGet();
    }
}
