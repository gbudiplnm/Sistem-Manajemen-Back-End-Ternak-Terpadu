package com.ternak.sapi.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URI;

@SpringBootConfiguration
public class HadoopConfig {
    @Bean
    public FileSystem fileSystem() throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://hadoop-master:9000"); // Ganti dengan URI NameNode Anda
        // Tambahkan konfigurasi lain jika diperlukan, misalnya autentikasi
        return FileSystem.get(URI.create("hdfs://hadoop-master:9000"), configuration);
    }
}
