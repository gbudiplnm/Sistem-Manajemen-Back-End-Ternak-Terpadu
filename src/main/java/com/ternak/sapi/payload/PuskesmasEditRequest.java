package com.ternak.sapi.payload;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PuskesmasEditRequest {
    private String namaPuskesmas;
    private String longitude;
    private String latitude;
    private String desa;
    private String kecamatan;
    private String[] dataLayanan;
    private String keterangan;
    private String kabupatenKota;
    private String provinsi;
    private String catatan;
}
