package com.config.mapping;

import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


public class Applications {
    /**es集群*/
    private ElasticsearchCluster elasticsearch;
    /**应用列表*/
    private List<Application> applicationlist = new ArrayList<>();

    public ElasticsearchCluster getElasticsearch() {
        return elasticsearch;
    }

    public void setElasticsearch(ElasticsearchCluster elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    public List<Application> getApplicationlist() {
        return applicationlist;
    }

    public void setApplicationlist(List<Application> applicationlist) {
        this.applicationlist = applicationlist;
    }
}
