/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ternak.sapi.repository;

import com.ternak.sapi.helper.HBaseCustomClient;
import java.util.List;
import com.ternak.sapi.model.LahanHijau;
import com.ternak.sapi.util.AppUtility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;

/**
 *
 * @author MSI MODERN 15 B13M
 */
public class LahanHijauRepository {

    Configuration conf = HBaseConfiguration.create();
    String tableName = "lahanHijauDev";

    public List<LahanHijau> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableLahanHijau = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(LahanHijau.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }
        return client.showListTable(tableLahanHijau.toString(), columnMapping, LahanHijau.class, size);
    }

    public LahanHijau save(LahanHijau lahanHijau) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = lahanHijau.getIdLahan();

        TableName tableLahanHijau = TableName.valueOf(tableName);
        client.insertRecord(tableLahanHijau, rowKey, "main", "idLahanHijau", rowKey);
        AppUtility.insertNonNullFields(lahanHijau, tableLahanHijau, rowKey, client);
        client.insertRecord(tableLahanHijau, rowKey, "detail", "created_by", "Polinema");
        return lahanHijau;
    }

    public List<LahanHijau> saveBulkImport(List<LahanHijau> lahanList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableLahanHijau = TableName.valueOf(tableName);

        System.out.println("Memulai penyimpanan data ke HBase...");
        List<String> failedRows = new ArrayList<>();
        for (LahanHijau lahan : lahanList) {
            try {
                AppUtility.insertNonNullFields(lahan, tableLahanHijau, tableName, client);
                client.insertRecord(tableLahanHijau, lahan.getIdLahan(), "detail", "created_by", "Polinema");

                System.out.println(
                        "Data berhasil disimpan ke HBase dengan ID Kelahiran (kelahiran): "
                        + lahan.getIdLahan());
            } catch (Exception e) {
                failedRows.add(lahan.getIdLahan());
                System.err.println(
                        "Failed to insert record for ID: " + lahan.getIdLahan()
                        + ", Error: " + e.getMessage());
            }
        }
        if (!failedRows.isEmpty()) {
            throw new IOException(
                    "Failed to save records for ID Lahan: " + String.join(", ", failedRows));
        }
         return lahanList;
    }
}
