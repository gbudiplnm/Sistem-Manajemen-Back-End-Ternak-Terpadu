package com.ternak.sapi.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.ternak.sapi.helper.HBaseCustomClient;
import com.ternak.sapi.model.User;
import com.ternak.sapi.util.AppUtility;

@Repository
public class UserRepository {
    @Autowired
    PasswordEncoder passwordEncoder;

    Configuration conf = HBaseConfiguration.create();
    String tableName = "userdev";

    public List<User> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("name", "name");
        columnMapping.put("nik","nik");
        columnMapping.put("username", "username");
        columnMapping.put("email", "email");
        columnMapping.put("password", "password");
        columnMapping.put("alamat","alamat");
        columnMapping.put("role", "role");
        return client.showListTable(tableUsers.toString(), columnMapping, User.class, size);
    }

    public List<User> findUsersNotUsedInLectures(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("name", "name");
        columnMapping.put("username", "username");
        columnMapping.put("email", "email");
        columnMapping.put("password", "password");
        columnMapping.put("role", "role");

        // Get the list of all users
        List<User> allUsers = client.showListTable(tableUsers.toString(), columnMapping, User.class, size);

        // Get the list of all user IDs that have been used in lectures
        Set<String> userIdsInLectures = new HashSet<>();
        Scan scan = new Scan();
        ResultScanner scanner = client.getTable("lectures").getScanner(scan);
        for (Result result : scanner) {
            byte[] userIdBytes = result.getValue(Bytes.toBytes("user"), Bytes.toBytes("id"));
            if (userIdBytes != null) {
                String userId = Bytes.toString(userIdBytes);
                userIdsInLectures.add(userId);
            }
        }
        scanner.close();

        // Find all users that have not been used in any lectures
        List<User> unusedUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (!userIdsInLectures.contains(user.getId())) {
                unusedUsers.add(user);
            }
        }

        return unusedUsers;
    }

    public User findByUsername(String username) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("name", "name");
        columnMapping.put("username", "username");
        columnMapping.put("email", "email");
        columnMapping.put("password", "password");
        columnMapping.put("role", "role");

        return client.getDataByColumn(tableUsers.toString(), columnMapping, "main", "username", username, User.class);
    }

    public User findByUserId(String userId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("password", "password");

        User user =  client.getDataByColumn(tableUsers.toString(), columnMapping, "main", "id", userId, User.class);

        return user.getId() != null ? user : null;
    }

    public User findByUserIdAll(String userId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        List<String> columns = AppUtility.getAttributeNames(User.class);
        // Add the mappings to the HashMap
        for (String column : columns) {
            if(column.equals("createdAt")) continue;
            columnMapping.put(column, column);
        }
        columnMapping.put("id", "id");
        columnMapping.put("password", "password");

        User user =  client.getDataByColumn(tableUsers.toString(), columnMapping, "main", "id", userId, User.class);

        return user.getId() != null ? user : null;
    }

    public boolean deleteById(String userId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        client.deleteRecord(tableName, userId);
        return true;
    }

    public User findById(String id) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("nik","nik");
        columnMapping.put("name", "name");
        columnMapping.put("username", "username");
        columnMapping.put("email", "email");
        columnMapping.put("password", "password");
        columnMapping.put("alamat","alamat");
        columnMapping.put("role", "role");

        return client.showDataTable(tableUsers.toString(), columnMapping, id, User.class);
    }

    public boolean existsByUsername(String username) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("nik", "nik");
        columnMapping.put("name", "name");
        columnMapping.put("username", "username");
        columnMapping.put("email", "email");
        columnMapping.put("password", "password");
        columnMapping.put("alamat", "alamat");
        columnMapping.put("role", "role");

        User user = client.getDataByColumn(tableUsers.toString(), columnMapping, "main", "username", username,
                User.class);
        if (user.getUsername() != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean existsByEmail(String email) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("nik", "nik");
        columnMapping.put("name", "name");
        columnMapping.put("username", "username");
        columnMapping.put("email", "email");
        columnMapping.put("password", "password");
        columnMapping.put("alamat", "alamat");
        columnMapping.put("role", "role");

        User user = client.getDataByColumn(tableUsers.toString(), columnMapping, "main", "email", email, User.class);
        if (user.getEmail() != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean existsByNik(String nik) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("nik", "nik");
        columnMapping.put("name", "name");
        columnMapping.put("username", "username");
        columnMapping.put("email", "email");
        columnMapping.put("password", "password");
        columnMapping.put("alamat", "alamat");
        columnMapping.put("role", "role");

        User user = client.getDataByColumn(tableUsers.toString(), columnMapping, "main", "nik", nik, User.class);
        if (user.getNik()!= null) {
            return true;
        } else {
            return false;
        }
    }

    public List<String> findExistingNik(List<String> nikList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableUser = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("nik", "nik");

        // Implementasi pencarian batch untuk NIK
        return client.getExistingByColumn(tableUser.toString(), columnMapping, "main", "nik", nikList);
    }
    public List<String> findExistingUsername(List<String> usernameList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableUser = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("username", "username");

        // Implementasi pencarian batch untuk NIK
        return client.getExistingByColumn(tableUser.toString(), columnMapping, "main", "username", usernameList);
    }

    public List<String> findExistingEmail(List<String> emailList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableUser = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("email", "email");

        // Implementasi pencarian batch untuk NIK
        return client.getExistingByColumn(tableUser.toString(), columnMapping, "main", "email", emailList);
    }

    public User save(User user) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = UUID.randomUUID().toString();

        TableName tableUsers = TableName.valueOf(tableName);
        client.insertRecord(tableUsers, rowKey, "main", "id", rowKey);
        client.insertRecord(tableUsers, rowKey, "main", "name", user.getName());
        client.insertRecord(tableUsers, rowKey, "main", "username", user.getUsername());
        client.insertRecord(tableUsers, rowKey, "main", "email", user.getEmail());
        client.insertRecord(tableUsers, rowKey, "main", "password", user.getPassword());
        client.insertRecord(tableUsers, rowKey, "main", "role", user.getRole());
        client.insertRecord(tableUsers, rowKey, "detail", "createdBy", user.getCreatedAt().toString());
        return user;
    }

    public User saveForm(User user) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = user.getId();

        TableName tableUsers = TableName.valueOf(tableName);
        client.insertRecord(tableUsers, rowKey, "main", "id", rowKey);
        client.insertRecord(tableUsers, rowKey, "main", "nik", user.getNik());
        client.insertRecord(tableUsers, rowKey, "main", "alamat", user.getAlamat());
        client.insertRecord(tableUsers, rowKey, "main", "name", user.getName());
        client.insertRecord(tableUsers, rowKey, "main", "username", user.getUsername());
        client.insertRecord(tableUsers, rowKey, "main", "email", user.getEmail());
        client.insertRecord(tableUsers, rowKey, "main", "password", user.getPassword());
        client.insertRecord(tableUsers, rowKey, "main", "role", user.getRole());
        client.insertRecord(tableUsers, rowKey, "detail", "createdBy", user.getCreatedAt().toString());
        return user;
    }

    public List<User> saveBulk(List<User> userList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableUsers = TableName.valueOf(tableName);

        System.out.println("Memulai penyimpanan data ke HBase...");
        List<String> failedRows = new ArrayList<>();

        for (User user : userList) {
            try {
                String rowKey = user.getId();
                client.insertRecord(tableUsers, rowKey, "main", "id", rowKey);
                client.insertRecord(tableUsers, rowKey, "main", "nik", user.getNik());
                client.insertRecord(tableUsers, rowKey, "main", "alamat", user.getAlamat());
                client.insertRecord(tableUsers, rowKey, "main", "name", user.getName());
                client.insertRecord(tableUsers, rowKey, "main", "username", user.getUsername());
                client.insertRecord(tableUsers, rowKey, "main", "email", user.getEmail());
                client.insertRecord(tableUsers, rowKey, "main", "password",user.getPassword().toString());
                client.insertRecord(tableUsers, rowKey, "main", "role", user.getRole().toString());
                client.insertRecord(tableUsers, rowKey, "detail", "createdBy", user.getCreatedAt().toString());

                System.out.println(
                        "Berhasil menyimpan user: " + user.getId());
            } catch (Exception e) {
                failedRows.add(user.getId());
                System.out.println("nik" + user.getNik());
                System.out.println("alamat" + user.getAlamat());
                System.out.println("nama" + user.getName());
                System.out.println("usernm" +user.getUsername());
                System.out.println("email" + user.getEmail());
                System.out.println("password" +user.getPassword());
                System.out.println( "role" +user.getRole());
                System.out.println("createdBy"+  user.getCreatedAt().toString());
                System.err.println(
                        "Failed to insert record for ID: " + user.getId()+ ", Error: "
                                + e.getMessage());
            }
        }

        if (!failedRows.isEmpty()) {
            throw new IOException("Failed to save records for User: " + String.join(", ", failedRows));
        }

        return userList;
    }

    public User update(String userId, User user) throws IOException{
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        client.insertRecord(tableUsers, userId, "main", "id", userId);
        client.insertRecord(tableUsers, userId, "main", "nik", user.getNik());
        client.insertRecord(tableUsers, userId, "main", "alamat", user.getAlamat());
        client.insertRecord(tableUsers, userId, "main", "name", user.getName());
        client.insertRecord(tableUsers, userId, "main", "username", user.getUsername());
        client.insertRecord(tableUsers, userId, "main", "email", user.getEmail());
        client.insertRecord(tableUsers, userId, "main", "password", user.getPassword());
        client.insertRecord(tableUsers, userId, "main", "role", user.getRole());
        client.insertRecord(tableUsers, userId, "detail", "createdBy", user.getCreatedAt().toString());
        return user;
    }

    private String safeString(String value) {
        return value != null ? value : "";
    }

}