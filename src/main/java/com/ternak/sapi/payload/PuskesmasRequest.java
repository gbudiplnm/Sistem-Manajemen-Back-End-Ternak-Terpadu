package com.ternak.sapi.payload;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PuskesmasRequest {
    private String namaPuskesmas;
    private String longitude;
    private String latitude;
    private String alamat;
    private String catatan;
    private MultipartFile[] filePath;
    private String petugasPencatat;
}
