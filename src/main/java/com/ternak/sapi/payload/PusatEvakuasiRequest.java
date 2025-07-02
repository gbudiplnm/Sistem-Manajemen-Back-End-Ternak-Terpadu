package com.ternak.sapi.payload;


import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PusatEvakuasiRequest {
    private String idPusatEvakuasi;
    private String namaPusatEvakuasi;
    private String tempatEvakuasiBencana;
    private String jenisBencana;
    private String petaEvakuasi;
    private String jalurEvakuasi;
    private String latitude;
    private String longitude;
    private String desa;
    private String kecamatan;
    private String kabupatenKota;
    private String provinsi;
    private String catatan;
    private MultipartFile[] filePath;
    private String petugasPencatat;
}
