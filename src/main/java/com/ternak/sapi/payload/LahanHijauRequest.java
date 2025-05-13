package com.ternak.sapi.payload;

import com.ternak.sapi.model.Peternak;
import com.ternak.sapi.model.Petugas;
import lombok.Data;
import enums.LahanStatus;

@Data
public class LahanHijauRequest {
    private String idLahan;    
    private String luasLahan;
    private String latitude;
    private String longitude;
    private String[] filePath;
    private String petugasInput;
    private String peternakInput;
    private String petugasReview;
    private LahanStatus statusLahan;
    private String alamat;
    private String catatan;
}
