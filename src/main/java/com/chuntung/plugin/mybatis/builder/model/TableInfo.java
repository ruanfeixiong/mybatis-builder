/*
 * Copyright (c) 2019 Tony Ho. Some rights reserved.
 */

package com.chuntung.plugin.mybatis.builder.model;

import java.util.List;

// table info
public class TableInfo {
    private String database;
    private String tableName;
    private String domainName;
    private String keyColumn;

    private List<ColumnInfo> customColumns;

    public TableInfo() {
    }

    public TableInfo(String database, String tableName) {
        this.database = database;
        this.tableName = tableName;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public List<ColumnInfo> getCustomColumns() {
        return customColumns;
    }

    public void setCustomColumns(List<ColumnInfo> customColumns) {
        this.customColumns = customColumns;
    }
}