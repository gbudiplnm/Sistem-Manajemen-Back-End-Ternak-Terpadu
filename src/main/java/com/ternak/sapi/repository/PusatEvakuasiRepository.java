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
import com.ternak.sapi.model.PusatEvakuasi;
import com.ternak.sapi.util.AppUtility;

public class PusatEvakuasiRepository {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "pusatEvakuasiDev";

    public List<PusatEvakuasi> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePusatEvakuasi = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(PusatEvakuasi.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }
        return client.showListTable(tablePusatEvakuasi.toString(), columnMapping, PusatEvakuasi.class, size);
    }

    public List<PusatEvakuasi> findAllUser(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePusatEvakuasi = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(PusatEvakuasi.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }


        return client.getDataListByColumn(tablePusatEvakuasi.toString(), columnMapping, "detail", "created_by", "Polinema", PusatEvakuasi.class, size);
    }

    public PusatEvakuasi save(PusatEvakuasi PusatEvakuasi) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = PusatEvakuasi.getIdPusatEvakuasi();

        TableName tablePusatEvakuasi = TableName.valueOf(tableName);
        client.insertRecord(tablePusatEvakuasi, rowKey, "main", "idPusatEvakuasi", rowKey);
        AppUtility.insertNonNullFields(PusatEvakuasi, tablePusatEvakuasi, rowKey, client);
        client.insertRecord(tablePusatEvakuasi, rowKey, "detail", "created_by", "Polinema");
        return PusatEvakuasi;
    }

    public List<PusatEvakuasi> saveBulkImport(List<PusatEvakuasi> lahanList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePusatEvakuasi = TableName.valueOf(tableName);

        System.out.println("Memulai penyimpanan data ke HBase...");
        List<String> failedRows = new ArrayList<>();
        for (PusatEvakuasi lahan : lahanList) {
            try {
                AppUtility.insertNonNullFields(lahan, tablePusatEvakuasi, tableName, client);
                client.insertRecord(tablePusatEvakuasi, lahan.getIdPusatEvakuasi(), "detail", "created_by", "Polinema");

                System.out.println(
                        "Data berhasil disimpan ke HBase dengan ID Pusat Evakuasi (pusatEvakuasi): "
                                + lahan.getIdPusatEvakuasi());
            } catch (Exception e) {
                failedRows.add(lahan.getIdPusatEvakuasi());
                System.err.println(
                        "Failed to insert record for ID: " + lahan.getIdPusatEvakuasi()
                                + ", Error: " + e.getMessage());
            }
        }
        if (!failedRows.isEmpty()) {
            throw new IOException(
                    "Failed to save records for ID Pusat Evakuasi: " + String.join(", ", failedRows));
        }
        return lahanList;
    }

    public PusatEvakuasi findPusatEvakuasiById(String id) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePusatEvakuasi = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(PusatEvakuasi.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }
        return client.getDataByColumn(tablePusatEvakuasi.toString(), columnMapping, id, "main", "idPusatEvakuasi",
                PusatEvakuasi.class);
    }
}
