package com.config.mapping;

public class AnalysField {
    /**同步分词字段*/
    private String field;
    /**同步分词策略*/
    private String analyzer;
    /**搜索分词策略*/
    private String searchanalyzer;

    public String getField() {
        return field;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    public String getSearchanalyzer() {
        return searchanalyzer;
    }

    public void setSearchanalyzer(String searchanalyzer) {
        this.searchanalyzer = searchanalyzer;
    }
}
