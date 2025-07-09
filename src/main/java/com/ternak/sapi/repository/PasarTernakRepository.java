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
import com.ternak.sapi.model.PasarTernak;
import com.ternak.sapi.util.AppUtility;


public class PasarTernakRepository {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "pasarTernakDev";

    public List<PasarTernak> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePasarTernak = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(PasarTernak.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }
        return client.showListTable(tablePasarTernak.toString(), columnMapping, PasarTernak.class, size);
    }

    public List<PasarTernak> findAllUser(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePasarTernak = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(PasarTernak.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }


        return client.getDataListByColumn(tablePasarTernak.toString(), columnMapping, "detail", "created_by", "Polinema", PasarTernak.class, size);
    }

    public PasarTernak save(PasarTernak PasarTernak) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = PasarTernak.getIdPasar();

        TableName tablePasarTernak = TableName.valueOf(tableName);
        client.insertRecord(tablePasarTernak, rowKey, "main", "idPasar", rowKey);
        AppUtility.insertNonNullFields(PasarTernak, tablePasarTernak, rowKey, client);
        client.insertRecord(tablePasarTernak, rowKey, "detail", "created_by", "Polinema");
        return PasarTernak;
    }

    public List<PasarTernak> saveBulkImport(List<PasarTernak> lahanList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePasarTernak = TableName.valueOf(tableName);

        System.out.println("Memulai penyimpanan data ke HBase...");
        List<String> failedRows = new ArrayList<>();
        for (PasarTernak lahan : lahanList) {
            try {
                AppUtility.insertNonNullFields(lahan, tablePasarTernak, tableName, client);
                client.insertRecord(tablePasarTernak, lahan.getIdPasar(), "detail", "created_by", "Polinema");

                System.out.println(
                        "Data berhasil disimpan ke HBase dengan ID Pasar (pasar): "
                                + lahan.getIdPasar());
            } catch (Exception e) {
                failedRows.add(lahan.getIdPasar());
                System.err.println(
                        "Failed to insert record for ID: " + lahan.getIdPasar()
                                + ", Error: " + e.getMessage());
            }
        }
        if (!failedRows.isEmpty()) {
            throw new IOException(
                    "Failed to save records for ID Pasar: " + String.join(", ", failedRows));
        }
        return lahanList;
    }

    public PasarTernak findPasarTernakById(String id) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePasarTernak = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        List<String> columns = AppUtility.getAttributeNames(PasarTernak.class);
        for (int i = 0; i < columns.size(); i++) {
            String a = columns.get(i);
            columnMapping.put(a, a);
        }
        return client.getDataByColumn(tablePasarTernak.toString(), columnMapping,  "main", "idPasar",id,
                PasarTernak.class);
    }
}
