package com.ternak.sapi.payload;

import com.ternak.sapi.util.AppUtility;

import lombok.Data;

@Data
public class AllLocationResponse {
    private String nama;
    private String longitude;
    private String latitude;
    private String namaClass;
    private String id;

    public AllLocationResponse() {
    }
    
    public AllLocationResponse(String nama, String longitude, String latitude, Class<?> namaClass) {
        this.nama = nama;
        this.longitude = longitude;
        this.latitude = latitude;
        this.namaClass = AppUtility.getClassNameAsString(namaClass);
    }

}
