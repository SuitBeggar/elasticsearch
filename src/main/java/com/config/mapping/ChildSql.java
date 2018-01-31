package com.config.mapping;

import lombok.Data;

@Data
public class ChildSql {
    /**名称*/
     private String  name;
    /**子表sql*/
    private String sql;
    /**子表对应的外键*/
    private String fk;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public String getFk() {
        return fk;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setFk(String fk) {
        this.fk = fk;
    }
}
