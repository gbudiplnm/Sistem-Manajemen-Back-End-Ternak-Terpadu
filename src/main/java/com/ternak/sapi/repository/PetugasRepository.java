package com.ternak.sapi.repository;

import com.ternak.sapi.helper.HBaseCustomClient;
import com.ternak.sapi.model.Hewan;
import com.ternak.sapi.model.JenisHewan;
import com.ternak.sapi.model.Peternak;
import com.ternak.sapi.model.Petugas;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;
import java.util.*;

public class PetugasRepository {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "petugasdev";

    public List<Petugas> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("petugasId", "petugasId");
        columnMapping.put("nikPetugas", "nikPetugas");
        columnMapping.put("namaPetugas", "namaPetugas");
        columnMapping.put("noTelp", "noTelp");
        columnMapping.put("email", "email");
        columnMapping.put("wilayah", "wilayah");
        columnMapping.put("job", "job");

        return client.showListTable(tablePetugas.toString(), columnMapping, Petugas.class, size);
    }

    public List<Petugas> findAllByUserID(String userID, int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("petugasId", "petugasId");
        columnMapping.put("nikPetugas", "nikPetugas");
        columnMapping.put("namaPetugas", "namaPetugas");
        columnMapping.put("noTelp", "noTelp");
        columnMapping.put("email", "email");
        columnMapping.put("job", "job");

        return client.getDataListByColumn(tablePetugas.toString(), columnMapping, "user", "id", userID, Petugas.class,
                size);
    }

    public Petugas save(Petugas petugas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = petugas.getPetugasId();

        TableName tablePetugas = TableName.valueOf(tableName);
        client.insertRecord(tablePetugas, rowKey, "main", "petugasId", petugas.getPetugasId());
        client.insertRecord(tablePetugas, rowKey, "main", "nikPetugas", petugas.getNikPetugas());
        client.insertRecord(tablePetugas, rowKey, "main", "namaPetugas", petugas.getNamaPetugas());
        client.insertRecord(tablePetugas, rowKey, "main", "noTelp", petugas.getNoTelp());
        client.insertRecord(tablePetugas, rowKey, "main", "email", petugas.getEmail());
        client.insertRecord(tablePetugas, rowKey, "main", "job", petugas.getJob());
        client.insertRecord(tablePetugas, rowKey, "main", "wilayah", petugas.getWilayah());
        client.insertRecord(tablePetugas, rowKey, "detail", "created_by", "Polinema");
        return petugas;
    }

    public Petugas saveNama(Petugas petugas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = petugas.getPetugasId() != null ? petugas.getPetugasId() : UUID.randomUUID().toString();

        TableName tablePetugas = TableName.valueOf(tableName);
        client.insertRecord(tablePetugas, rowKey, "main", "petugasId", rowKey);
        client.insertRecord(tablePetugas, rowKey, "main", "nikPetugas", petugas.getNikPetugas());
        client.insertRecord(tablePetugas, rowKey, "main", "namaPetugas", petugas.getNamaPetugas());
        client.insertRecord(tablePetugas, rowKey, "main", "noTelp", petugas.getNoTelp());
        client.insertRecord(tablePetugas, rowKey, "main", "email", petugas.getEmail());
        client.insertRecord(tablePetugas, rowKey, "main", "job", petugas.getJob());
        client.insertRecord(tablePetugas, rowKey, "main", "wilayah", petugas.getWilayah());
        client.insertRecord(tablePetugas, rowKey, "detail", "created_by", "Polinema");
        return petugas;
    }

    public Petugas saveImport(Petugas petugas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = petugas.getPetugasId() != null ? petugas.getPetugasId() : UUID.randomUUID().toString();

        TableName tablePetugas = TableName.valueOf(tableName);
        if (!existsByNamaPetugas(petugas.getNamaPetugas())) {
            client.insertRecord(tablePetugas, rowKey, "main", "petugasId", rowKey);
            client.insertRecord(tablePetugas, rowKey, "main", "nikPetugas", petugas.getNikPetugas());
            client.insertRecord(tablePetugas, rowKey, "main", "namaPetugas", petugas.getNamaPetugas());
            client.insertRecord(tablePetugas, rowKey, "main", "noTelp", petugas.getNoTelp());
            client.insertRecord(tablePetugas, rowKey, "main", "email", petugas.getEmail());
            client.insertRecord(tablePetugas, rowKey, "main", "job", petugas.getJob());
            client.insertRecord(tablePetugas, rowKey, "main", "wilayah", petugas.getWilayah());
            client.insertRecord(tablePetugas, rowKey, "detail", "created_by", "Polinema");
        }
        return petugas;
    }

    public List<Petugas> saveByNama(List<Petugas> petugasList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);

        System.out.println("Memulai penyimpanan data ke HBase...");
        List<String> failedRows = new ArrayList<>();

        for (Petugas petugas : petugasList) {
            try {
                if (petugas.getNamaPetugas() == null) {
                    System.out.println("Data tidak lengkap, melewati penyimpanan Nama: " + petugas.getNamaPetugas());
                    continue;
                }

                String rowKey = petugas.getPetugasId() != null ? petugas.getPetugasId() : UUID.randomUUID().toString();

                // Insert records into HBase
                client.insertRecord(tablePetugas, rowKey, "main", "petugasId", rowKey);
                client.insertRecord(tablePetugas, rowKey, "main", "nikPetugas", petugas.getNikPetugas());
                client.insertRecord(tablePetugas, rowKey, "main", "namaPetugas", petugas.getNamaPetugas());
                client.insertRecord(tablePetugas, rowKey, "main", "noTelp", petugas.getNoTelp());
                client.insertRecord(tablePetugas, rowKey, "main", "email", petugas.getEmail());
                client.insertRecord(tablePetugas, rowKey, "main", "job", petugas.getJob());
                client.insertRecord(tablePetugas, rowKey, "main", "wilayah", petugas.getWilayah());
                client.insertRecord(tablePetugas, rowKey, "detail", "created_by", "Polinema");

                System.out.println("Berhasil menyimpan Nama: " + petugas.getNamaPetugas());
            } catch (Exception e) {
                failedRows.add(petugas.getNamaPetugas());
                System.err.println("Gagal menyimpan Nama: " + petugas.getNamaPetugas() + ", Error: " + e.getMessage());
            }
        }

        if (!failedRows.isEmpty()) {
            System.err.println("Proses selesai dengan beberapa kegagalan. Total gagal: " + failedRows.size());
            System.err.println("Nama Petugas yang gagal disimpan: " + String.join(", ", failedRows));
        } else {
            System.out.println("Semua data berhasil disimpan ke HBase.");
        }

        return petugasList;
    }

    public List<Petugas> saveAll(List<Petugas> petugasList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);

        System.out.println("Memulai penyimpanan data ke HBase...");
        List<String> failedRows = new ArrayList<>();

        for (Petugas petugas : petugasList) {
            try {
                if (petugas.getNikPetugas() == null || petugas.getNamaPetugas() == null ||
                        petugas.getNoTelp() == null || petugas.getEmail() == null) {
                    System.out.println("Data tidak lengkap, melewati penyimpanan NIK: " + petugas.getNikPetugas());
                    continue;
                }

                String rowKey = petugas.getPetugasId() != null ? petugas.getPetugasId() : UUID.randomUUID().toString();

                // Insert records into HBase
                client.insertRecord(tablePetugas, rowKey, "main", "petugasId", rowKey);
                client.insertRecord(tablePetugas, rowKey, "main", "nikPetugas", petugas.getNikPetugas());
                client.insertRecord(tablePetugas, rowKey, "main", "namaPetugas", petugas.getNamaPetugas());
                client.insertRecord(tablePetugas, rowKey, "main", "noTelp", petugas.getNoTelp());
                client.insertRecord(tablePetugas, rowKey, "main", "email", petugas.getEmail());
                client.insertRecord(tablePetugas, rowKey, "main", "job", petugas.getJob());
                client.insertRecord(tablePetugas, rowKey, "main", "wilayah", petugas.getWilayah());
                client.insertRecord(tablePetugas, rowKey, "detail", "created_by", "Polinema");

                System.out.println("Berhasil menyimpan NIK: " + petugas.getNikPetugas());
            } catch (Exception e) {
                failedRows.add(petugas.getNikPetugas());
                System.err.println("Gagal menyimpan NIK: " + petugas.getNikPetugas() + ", Error: " + e.getMessage());
            }
        }

        if (!failedRows.isEmpty()) {
            System.err.println("Proses selesai dengan beberapa kegagalan. Total gagal: " + failedRows.size());
            System.err.println("NIK yang gagal disimpan: " + String.join(", ", failedRows));
        } else {
            System.out.println("Semua data berhasil disimpan ke HBase.");
        }

        return petugasList;
    }

    public Petugas findById(String petugasId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("petugasId", "petugasId");
        columnMapping.put("nikPetugas", "nikPetugas");
        columnMapping.put("namaPetugas", "namaPetugas");
        columnMapping.put("noTelp", "noTelp");
        columnMapping.put("email", "email");
        columnMapping.put("job", "job");
        columnMapping.put("wilayah", "wilayah");

        Petugas petugas = client.getDataByColumn(tablePetugas.toString(), columnMapping, "main",
                "petugasId", petugasId, Petugas.class);
        return petugas.getPetugasId() != null ? petugas : null; // Jika ada data dengan nikPetugas yang sama
    }

    public Petugas update(String petugasId, Petugas petugas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        System.out.println("Data " + petugasId + petugas.getNamaPetugas() + petugas.getNikPetugas() + petugas.getEmail()
                + petugas.getWilayah() + petugas.getJob() + petugas.getNoTelp());

        // client.insertRecord(tablePetugas, petugasId, "main", "petugasId",petugasId);
        client.insertRecord(tablePetugas, petugasId, "main", "nikPetugas", petugas.getNikPetugas());
        client.insertRecord(tablePetugas, petugasId, "main", "namaPetugas", petugas.getNamaPetugas());
        client.insertRecord(tablePetugas, petugasId, "main", "noTelp", petugas.getNoTelp());
        client.insertRecord(tablePetugas, petugasId, "main", "email", petugas.getEmail());
        client.insertRecord(tablePetugas, petugasId, "main", "job", petugas.getJob());
        client.insertRecord(tablePetugas, petugasId, "main", "wilayah", petugas.getWilayah());
        client.insertRecord(tablePetugas, petugasId, "detail", "created_by", "Polinema");
        return petugas;
    }

    public boolean existsByUserID(String UID) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");

        Petugas petugas = client.getDataByColumn(tablePetugas.toString(), columnMapping, "user", "id", UID,
                Petugas.class);
        if (petugas.getNikPetugas() != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteById(String idPetugas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        client.deleteRecord(tableName, idPetugas);
        return true;
    }

    public boolean existsByNikPetugas(String nikPetugas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("nikPetugas", "nikPetugas");

        Petugas petugas = client.getDataByColumn(tablePetugas.toString(), columnMapping, "main", "nikPetugas",
                nikPetugas, Petugas.class);
        return petugas.getNikPetugas() != null; // Jika ada data dengan nikPetugas yang sama
    }

    public Petugas findByNik(String nikPetugas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("petugasId", "petugasId");
        columnMapping.put("nikPetugas", "nikPetugas");
        columnMapping.put("namaPetugas", "namaPetugas");
        columnMapping.put("noTelp", "noTelp");
        columnMapping.put("email", "email");
        columnMapping.put("wilayah", "wilayah");
        columnMapping.put("job", "job");

        Petugas petugas = client.getDataByColumn(tablePetugas.toString(), columnMapping, "main", "nikPetugas",
                nikPetugas, Petugas.class);
        return petugas.getNikPetugas() != null ? petugas : null; // Jika ada data dengan nikPetugas yang sama
    }

    public Petugas findByNamaPetugas(String namaPetugas) throws IOException {

        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        columnMapping.put("petugasId", "petugasId");
        columnMapping.put("nikPetugas", "nikPetugas");
        columnMapping.put("namaPetugas", "namaPetugas");
        columnMapping.put("noTelp", "noTelp");
        columnMapping.put("email", "email");
        columnMapping.put("job", "job");

        Petugas petugas = client.getDataByColumn(tablePetugas.toString(), columnMapping, "main", "namaPetugas",
                namaPetugas,
                Petugas.class);

        return petugas.getNamaPetugas() != null ? petugas : null;
    }

    public boolean existsByEmail(String email) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("email", "email");

        Petugas petugas = client.getDataByColumn(tablePetugas.toString(), columnMapping, "main", "email", email,
                Petugas.class);
        return petugas.getEmail() != null; // Jika ada data dengan email yang sama
    }

    public boolean existsByNamaPetugas(String namaPetugas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("namaPetugas", "namaPetugas");

        Petugas petugas = client.getDataByColumn(tablePetugas.toString(), columnMapping, "main", "namaPetugas",
                namaPetugas,
                Petugas.class);
        return petugas.getNamaPetugas() != null; // Jika ada data dengan email yang sama
    }

    public boolean existsByNoTelp(String noTelp) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("noTelp", "noTelp");

        Petugas petugas = client.getDataByColumn(tablePetugas.toString(), columnMapping, "main", "noTelp", noTelp,
                Petugas.class);
        return petugas.getNoTelp() != null; // Jika ada data dengan noTelp yang sama
    }

    public List<String> findExistingNik(List<String> nikList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("nikPetugas", "nikPetugas");

        // Implementasi pencarian batch untuk NIK
        return client.getExistingByColumn(tablePetugas.toString(), columnMapping, "main", "nikPetugas", nikList);
    }

    public List<String> findExistingEmail(List<String> emailList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("email", "email");

        // Implementasi pencarian batch untuk Email
        return client.getExistingByColumn(tablePetugas.toString(), columnMapping, "main", "email", emailList);
    }

    public List<String> findExistingNoTelp(List<String> noTelpList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tablePetugas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("noTelp", "noTelp");

        // Implementasi pencarian batch untuk NoTelp
        return client.getExistingByColumn(tablePetugas.toString(), columnMapping, "main", "noTelp", noTelpList);
    }
}
