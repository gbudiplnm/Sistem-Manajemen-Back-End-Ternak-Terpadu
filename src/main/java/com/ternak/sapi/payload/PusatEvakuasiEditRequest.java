package com.ternak.sapi.payload;


import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PusatEvakuasiEditRequest {
    private String namaPusatEvakuasi;
    private String latitude;
    private String longitude;
    private String desa;
    private String kecamatan;
    private String kabupatenKota;
    private String provinsi;
    private String catatan;
    private String tempatEvakuasiBencana;
    private String jenisBencana;
    private String petaEvakuasi;
    private String jalurEvakuasi;

}
