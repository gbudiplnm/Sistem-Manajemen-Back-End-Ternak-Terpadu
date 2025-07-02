package com.ternak.sapi.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;

import com.ternak.sapi.helper.HBaseCustomClient;
import com.ternak.sapi.model.Puskesmas;
import com.ternak.sapi.util.AppUtility;

public class PuskesmasRepository {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "puskesmasDev";

    public List<Puskesmas> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePuskesmas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(Puskesmas.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }
        return client.showListTable(tablePuskesmas.toString(), columnMapping, Puskesmas.class, size);
    }

    public List<Puskesmas> findAllUser(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePuskesmas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(Puskesmas.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }


        return client.getDataListByColumn(tablePuskesmas.toString(), columnMapping, "detail", "created_by", "Polinema", Puskesmas.class, size);
    }

    public Puskesmas save(Puskesmas puskesmas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = puskesmas.getIdPuskesmas();

        TableName tablePuskesmas = TableName.valueOf(tableName);
        client.insertRecord(tablePuskesmas, rowKey, "main", "idPuskesmas", rowKey);
        AppUtility.insertNonNullFields(puskesmas, tablePuskesmas, rowKey, client);
        client.insertRecord(tablePuskesmas, rowKey, "detail", "created_by", "Polinema");
        return puskesmas;
    }

    public List<Puskesmas> saveBulkImport(List<Puskesmas> lahanList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePuskesmas = TableName.valueOf(tableName);

        System.out.println("Memulai penyimpanan data ke HBase...");
        List<String> failedRows = new ArrayList<>();
        for (Puskesmas lahan : lahanList) {
            try {
                AppUtility.insertNonNullFields(lahan, tablePuskesmas, tableName, client);
                client.insertRecord(tablePuskesmas, lahan.getIdPuskesmas(), "detail", "created_by", "Polinema");

                System.out.println(
                        "Data berhasil disimpan ke HBase dengan ID Puskesmas (puskesmas): "
                                + lahan.getIdPuskesmas());
            } catch (Exception e) {
                failedRows.add(lahan.getIdPuskesmas());
                System.err.println(
                        "Failed to insert record for ID: " + lahan.getIdPuskesmas()
                                + ", Error: " + e.getMessage());
            }
        }
        if (!failedRows.isEmpty()) {
            throw new IOException(
                    "Failed to save records for ID Puskesmas: " + String.join(", ", failedRows));
        }
        return lahanList;
    }

    public Puskesmas findPuskesmasById(String id) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePuskesmas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(Puskesmas.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }
        return client.getDataByColumn(tablePuskesmas.toString(), columnMapping,  "main", "idPuskesmas",id,
                Puskesmas.class);
    }
}
