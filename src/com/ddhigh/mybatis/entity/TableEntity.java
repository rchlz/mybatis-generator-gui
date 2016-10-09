package com.ddhigh.mybatis.entity;

public class TableEntity {
    private String tableName;
    private Boolean isSelected = false;
    private String entityName;

    public TableEntity(String tableName, String entityName) {
        this.tableName = tableName;
        this.entityName = entityName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
