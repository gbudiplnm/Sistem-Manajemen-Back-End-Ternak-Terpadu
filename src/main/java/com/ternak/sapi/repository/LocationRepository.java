package com.ternak.sapi.repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.springframework.stereotype.Repository;

import com.ternak.sapi.helper.HBaseCustomClient;
import com.ternak.sapi.payload.AllLocationResponse;
import com.ternak.sapi.payload.TableNameAndNameColumn;
import com.ternak.sapi.util.AppUtility;

@Repository
public class LocationRepository {

    Configuration conf = HBaseConfiguration.create();

    public List<AllLocationResponse> getAllLocation(TableNameAndNameColumn tableNameAndNameColumn, Class<?> className)
            throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String tableName = tableNameAndNameColumn.getTableName();
        TableName tableName2 = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = Arrays.asList(tableNameAndNameColumn.getNameColumn(),
                tableNameAndNameColumn.getIdColumn(), "latitude", "longitude");
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }
        List<?> result = client.showListTable(tableName2.toString(), columnMapping, className, -1);
        List<AllLocationResponse> responses = new ArrayList<>();
        for (Object o : result) {
            if (result.getClass() == className) {

            }
            Map<String, String> map = AppUtility.convertObjectToMap(o);
            AllLocationResponse response = new AllLocationResponse();
            response.setLatitude(map.get("latitude"));
            response.setLongitude(map.get("longitude"));
            response.setId(map.get(tableNameAndNameColumn.getIdColumn()));
            response.setNama(map.get(tableNameAndNameColumn.getNameColumn()));
            response.setNamaClass(AppUtility.getClassNameAsString(className));
            responses.add(response);
        }
        return responses;

    }
}
