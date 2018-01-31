package com.config.mapping;

import java.util.ArrayList;
import java.util.List;

public class Application {
    /**索引名称*/
    private String indexname;
    /**动态数据源*/
    private SycnDataSource datasource;
    /**索引类型列表*/
    private List<IndexType> indextypelist = new ArrayList<>();

    public String getIndexname() {
        return indexname;
    }

    public SycnDataSource getDatasource() {
        return datasource;
    }

    public List<IndexType> getIndextypelist() {
        return indextypelist;
    }

    public void setIndexname(String indexname) {
        this.indexname = indexname;
    }

    public void setDatasource(SycnDataSource datasource) {
        this.datasource = datasource;
    }

    public void setIndextypelist(List<IndexType> indextypelist) {
        this.indextypelist = indextypelist;
    }
}
