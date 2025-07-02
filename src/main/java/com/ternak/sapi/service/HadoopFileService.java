package com.ternak.sapi.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.PostConstruct;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ternak.sapi.config.PathConfig;

@Service
public class HadoopFileService {

    @Value("${hdfs.base.url}")
    private String basePath;

    public boolean deleteFile(String fileName) {
        String uri = basePath + "/" + fileName;
        Configuration configuration = new Configuration();
        try {
            FileSystem fs = FileSystem.get(URI.create(uri), configuration);
            Path filePath = new Path(uri);
            return fs.delete(filePath, false);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResponseEntity<byte[]> getFileFromHDFS(String fileName) {
        String uri = basePath + "/" + fileName;
        Configuration configuration = new Configuration();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            FileSystem fs = FileSystem.get(URI.create(uri), configuration);
            Path filePath = new Path(uri);

            if (!fs.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            InputStream inputStream = fs.open(filePath);
            IOUtils.copyBytes(inputStream, outputStream, 4096, false);
            inputStream.close();
            fs.close();

            byte[] fileBytes = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(fileBytes.length);

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public String uploadFile(String id, MultipartFile file, String prefix) throws IOException {
        try {
            // Mendapatkan nama file asli
            String originalFileName = file.getOriginalFilename();

            // Memeriksa apakah originalFileName tidak null
            if (originalFileName == null) {
                throw new IllegalArgumentException("File name is null");
            }

            // Mendapatkan ekstensi file
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            // Mendapatkan timestamp saat ini
            String timestamp = String.valueOf(System.currentTimeMillis());

            // Membuat UUID baru

            // Menggabungkan timestamp dan UUID
            String newFileName = "file_" + timestamp + "_" + id;
            String filePath = PathConfig.storagePath + "/" + newFileName + fileExtension;
            File newFile = new File(filePath);

            // Menyimpan file ke lokasi yang ditentukan di server
            file.transferTo(newFile);

            // Mendapatkan local path dari file yang disimpan
            String localPath = newFile.getAbsolutePath();
            String uri = "hdfs://hadoop-master:9000";
            String hdfsDir = "hdfs://hadoop-master:9000/file/" + prefix + "/" + newFileName + fileExtension;
            Configuration configuration = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(uri), configuration);
            fs.copyFromLocalFile(new Path(localPath), new Path(hdfsDir));
            String savePath = "file/" + prefix + "/" + newFileName + fileExtension;

            newFile.delete();
            return savePath;
        } catch (IOException e) {
            // Penanganan kesalahan saat menyimpan file
            e.printStackTrace();
            throw e;
        }
    }
}
