package com.ternak.sapi.payload;

import lombok.Data;

@Data
public class PuskesmasRequest {
    private String namaPuskesmas;
    private String longitude;
    private String latitude;
    private String alamat;
    private String idPuskesmas;
    private String catatan;
    private String[] filePath = new String[5];
    private String petugasPencatat;
}
