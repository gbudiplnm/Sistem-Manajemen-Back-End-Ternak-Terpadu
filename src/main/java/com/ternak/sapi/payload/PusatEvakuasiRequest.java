package com.ternak.sapi.payload;


import lombok.Data;

@Data
public class PusatEvakuasiRequest {
    private String idPusatEvakuasi;
    private String namaPusatEvakuasi;
    private String latitude;
    private String longitude;
    private String alamatPusatEvakuasi;
    private String catatan;
    private String[] filePath = new String[5];
    private String petugasPencatat;
}
