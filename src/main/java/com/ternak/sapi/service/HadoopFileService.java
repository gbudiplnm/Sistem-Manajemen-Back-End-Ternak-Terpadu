package com.ternak.sapi.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ternak.sapi.config.PathConfig;

@Service
public class HadoopFileService {

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
            String savePath = "file/" + prefix + "/"+ newFileName + fileExtension;

            newFile.delete();
            return savePath;
        } catch (IOException e) {
            // Penanganan kesalahan saat menyimpan file
            e.printStackTrace();
            throw e;
        }
    }
}
