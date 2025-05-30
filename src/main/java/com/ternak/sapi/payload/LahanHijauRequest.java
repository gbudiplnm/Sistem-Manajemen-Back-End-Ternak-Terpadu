package com.ternak.sapi.payload;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

import enums.LahanStatus;

@Data
public class LahanHijauRequest {
    private String idLahan;    
    private String luasLahan;
    private String latitude;
    private String longitude;
    private MultipartFile[] filePath;
    private String petugasInput;
    private String peternakInput;
    private String petugasReview;
    private LahanStatus statusLahan;
    private String alamat;
    private String kecamatan;
    private String desa;
    private String kabupatenKota;
    private String provinsi;
    private String catatan;
}
