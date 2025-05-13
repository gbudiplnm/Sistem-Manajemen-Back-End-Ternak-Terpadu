package com.ternak.sapi.payload;


import lombok.Data;

@Data
public class PasarTernakRequest {
    private String namaPasar;
    private String idPasar;
    private String latitude;
    private String[] filePath;
    private String longitude;
    private String catatan;
    private String alamatPasar;
    private String petugasPencatat;
}
