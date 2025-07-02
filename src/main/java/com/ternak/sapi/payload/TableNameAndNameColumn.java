package com.ternak.sapi.payload;

import lombok.Data;

@Data
public class TableNameAndNameColumn{
    private String tableName;
    private String nameColumn;
    private Class<?> classObject;
    private String idColumn;

    public TableNameAndNameColumn(String tableName, String nameColumn, String idColumn, Class<?> classObject) {
        this.idColumn = idColumn;
        this.classObject = classObject;
        this.tableName = tableName;
        this.nameColumn = nameColumn;
    }
}
