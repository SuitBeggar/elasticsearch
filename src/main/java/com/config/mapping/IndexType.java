package com.config.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexType {
    /**索引类型*/
    private String  name;
    /**索引名称*/
    private String indexName;
    /**主表sql*/
    private String  primarysql;
    /**主表主键*/
    private String primarykey;
    /**主表主键sql*/
    private String primarykeysql;
    /**增量sql*/
    private String incrementsql;
    /**增量同步时间差*/
    private String  timediffer;
    /**增量同步定时*/
    private String scheduled;
    /**增量同步开关*/
    private Boolean syncswitch;
    /**增量同步线程数*/
    private int syncthreadcount;
    /**子表列表*/
    private List<ChildSql> childsqlmap = new ArrayList<>();
    /**搜索字段数组*/
    private String[] searchfield;
     /**搜索高亮数组*/
    private String[] highfield;
    private List<AnalysField> analysfield;

    public String getName() {
        return name;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getPrimarysql() {
        return primarysql;
    }

    public String getPrimarykey() {
        return primarykey;
    }

    public String getPrimarykeysql() {
        return primarykeysql;
    }

    public String getIncrementsql() {
        return incrementsql;
    }

    public String getTimediffer() {
        return timediffer;
    }

    public String getScheduled() {
        return scheduled;
    }

    public Boolean getSyncswitch() {
        return syncswitch;
    }

    public int getSyncthreadcount() {
        return syncthreadcount;
    }

    public List<ChildSql> getChildsqlmap() {
        return childsqlmap;
    }

    public String[] getSearchfield() {
        return searchfield;
    }

    public String[] getHighfield() {
        return highfield;
    }

    public List<AnalysField> getAnalysfield() {
        return analysfield;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setPrimarysql(String primarysql) {
        this.primarysql = primarysql;
    }

    public void setPrimarykey(String primarykey) {
        this.primarykey = primarykey;
    }

    public void setPrimarykeysql(String primarykeysql) {
        this.primarykeysql = primarykeysql;
    }

    public void setIncrementsql(String incrementsql) {
        this.incrementsql = incrementsql;
    }

    public void setTimediffer(String timediffer) {
        this.timediffer = timediffer;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public void setSyncswitch(Boolean syncswitch) {
        this.syncswitch = syncswitch;
    }

    public void setSyncthreadcount(int syncthreadcount) {
        this.syncthreadcount = syncthreadcount;
    }

    public void setChildsqlmap(List<ChildSql> childsqlmap) {
        this.childsqlmap = childsqlmap;
    }

    public void setSearchfield(String[] searchfield) {
        this.searchfield = searchfield;
    }

    public void setHighfield(String[] highfield) {
        this.highfield = highfield;
    }

    public void setAnalysfield(List<AnalysField> analysfield) {
        this.analysfield = analysfield;
    }
}
