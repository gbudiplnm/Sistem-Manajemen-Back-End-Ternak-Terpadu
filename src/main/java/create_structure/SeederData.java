package create_structure;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
//import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hadoop.fs.FileSystem;
import java.net.URI;
import org.apache.hadoop.fs.Path;

import org.apache.http.client.fluent.Request;
//import org.json.JSONArray;
//import org.json.JSONObject;

import com.github.javafaker.Faker;
import com.ternak.sapi.config.PathConfig;
import java.io.File;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class SeederData {

    private static String jenisHewan;
      public static void main(String[] args)  throws Exception {

        Configuration conf = HBaseConfiguration.create();
        HBaseCustomClient client = new HBaseCustomClient(conf);

        // time now
        ZoneId zoneId = ZoneId.of("Asia/Jakarta");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        Instant instant = zonedDateTime.toInstant();
        
        Faker faker = new Faker(new Locale("id-ID"));
        Random random = new Random();

        // define name all table;
        TableName tableBerita = TableName.valueOf("beritas");
        TableName tableHewan = TableName.valueOf("hewans");
         TableName tableJenisHewan = TableName.valueOf("jenishewans");
        TableName tableInseminasi = TableName.valueOf("inseminasis");
        TableName tableKandang = TableName.valueOf("kandangs");
        TableName tableKelahiran = TableName.valueOf("kelahirans");
        TableName tablePengobatan = TableName.valueOf("pengobatans");
        TableName tablePeternak = TableName.valueOf("peternaks");
        TableName tablePetugas = TableName.valueOf("petugass");
        TableName tablePkb = TableName.valueOf("pkbs");
        TableName tableVaksin = TableName.valueOf("vaksins");
        TableName tableUser = TableName.valueOf("users");

        // ==============================================================================================
        // INSERT DATA
        // ==============================================================================================
        
         // Insert Petugas 
        client.insertRecord(tablePetugas, "PTG001", "main", "nikPetugas", "PTG001");
        client.insertRecord(tablePetugas, "PTG001", "main", "namaPetugas", "sunaryam");
        client.insertRecord(tablePetugas, "PTG001", "main", "noTelp", "123456789");
        client.insertRecord(tablePetugas, "PTG001", "main", "email", "petugas1@gmail.com");
        client.insertRecord(tablePetugas, "PTG001", "detail", "created_by", "admin");
        
        //    Insert Peternak
        client.insertRecord(tablePeternak, "PTK002", "main", "idPeternak", "PTK002");
        client.insertRecord(tablePeternak, "PTK002", "main", "nikPeternak", "Ptk002");
        client.insertRecord(tablePeternak, "PTK002", "main", "namaPeternak", "peternak1");
        client.insertRecord(tablePeternak, "PTK002", "main", "idISIKHNAS", "2211");
        client.insertRecord(tablePeternak, "PTK002", "main", "lokasi", "Lumajang");
        client.insertRecord(tablePeternak, "PTK002", "main", "tanggalPendaftaran", "22/02/2022");
        client.insertRecord(tablePeternak, "PTK002", "petugas", "nikPetugas", "PTG001");
        client.insertRecord(tablePeternak, "PTK002", "petugas", "namaPetugas", "sunaryam");
        client.insertRecord(tablePeternak, "PTK002", "petugas", "noTelp", "123456789");
        client.insertRecord(tablePeternak, "PTK002", "petugas", "email", "petugas1@gmail.com");
          
//        insert kandang
        for (int i = 0; i < 100; i++) {

            String rowKey = "KND" + faker.idNumber().valid();
            String idKandang = rowKey;
            String luas = "200";
            String kapasitas = faker.number().digits(2);
            String nilaiBangunan = faker.currency().name();
            double latitude = getRandomLatitude();
            double longitude = getRandomLongitude();
            String latitudeString = String.format("%.6f", latitude);
            String longitudeString = String.format("%.6f", longitude);

            String url = "https://nominatim.openstreetmap.org/reverse?lat=" + latitudeString + "&lon=" + longitudeString + "&format=json";
            String response = Request.Get(url).execute().returnContent().asString();
            JSONObject json = new JSONObject(response);
            String fullAddress = json.getString("display_name");

            String[] addressComponents = fullAddress.split(",");
            String desa = addressComponents.length > 0 ? addressComponents[0].trim() : "";
            String kecamatan = addressComponents.length > 1 ? addressComponents[1].trim() : "";
            String kabupaten = addressComponents.length > 2 ? addressComponents[2].trim() : "";
            String provinsi = addressComponents.length > 3 ? addressComponents[3].trim() : "";
            
            String timestamp = String.valueOf(System.currentTimeMillis());
//
                // Membuat UUID baru
                String uuid = UUID.randomUUID().toString();

                // Menggabungkan timestamp dan UUID
                String newFileName = "file_" + timestamp + "_" + uuid;

                String localPath = "E:/Skripsi/test.jpg";
                String uri = "hdfs://hadoop-master:9000";
                String hdfsDir = "hdfs://hadoop-master:9000/kandang/"+newFileName+".jpg";
                Configuration configuration = new Configuration();
                FileSystem fs = FileSystem.get(URI.create(uri), configuration);
                fs.copyFromLocalFile(new Path(localPath), new Path(hdfsDir));
                String savePath = "file/kandang/"+ newFileName + ".jpg";

//            String photoUrl = "https://picsum.photos/200";
//            String photoFilePath = Paths.get("/file/kandang/", idKandang + ".jpg").toString();
//            downloadFakePhoto(photoUrl, photoFilePath);
//            String foto = photoFilePath;

            client.insertRecord(tableKandang, rowKey, "main", "idKandang", idKandang);
            client.insertRecord(tableKandang, rowKey, "main", "luas", luas);
            client.insertRecord(tableKandang, rowKey, "main", "jenisHewan", jenisHewan);
            client.insertRecord(tableKandang, rowKey, "main", "kapasitas", kapasitas);
            client.insertRecord(tableKandang, rowKey, "main", "nilaiBangunan", nilaiBangunan);
            client.insertRecord(tableKandang, rowKey, "main", "alamat", fullAddress);
            client.insertRecord(tableKandang, rowKey, "main", "latitude", latitudeString);
            client.insertRecord(tableKandang, rowKey, "main", "longitude", longitudeString);
            client.insertRecord(tableKandang, rowKey, "peternak", "idPeternak", "PTK002");
            client.insertRecord(tableKandang, rowKey, "peternak", "nikPeternak", "Ptk002");
            client.insertRecord(tableKandang, rowKey, "peternak", "namaPeternak", "peternak1");
            client.insertRecord(tableKandang, rowKey, "peternak", "idISIKHNAS", "2211");
            client.insertRecord(tableKandang, rowKey, "main", "file_path", savePath);
            
        }
    }
    
//========================================================================================================================

    private static String getRandomLumajangLocation(List<String> locations, Random random) {
        int index = random.nextInt(locations.size());
        return locations.get(index);
    }

    private static String getRandomLuasKandang(List<String> luas, Random random) {
        int index = random.nextInt(luas.size());
        return luas.get(index);
    }
    
//    private static void downloadFakePhoto(String photoUrl, String destinationFilePath) throws IOException {
//        Path destinationPath = Paths.get(destinationFilePath).getParent();
//        if (!Files.exists(destinationPath)) {
//            Files.createDirectories(destinationPath);
//        }
//        URL url = new URL(photoUrl);
//        Files.copy(url.openStream(), Paths.get(destinationFilePath), StandardCopyOption.REPLACE_EXISTING);
//    }

    private static double getRandomLatitude() {
    double minLatitude = -7.978298;
    double maxLatitude = -8.288940;
    return minLatitude + (Math.random() * (maxLatitude - minLatitude));
}


    private static double getRandomLongitude() {
        double minLongitude = 112.921044;
        double maxLongitude = 113.367704;
        return minLongitude + (Math.random() * (maxLongitude - minLongitude));
    }
   
       
    

}
