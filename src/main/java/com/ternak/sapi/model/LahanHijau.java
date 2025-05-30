/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ternak.sapi.model;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import enums.LahanStatus;
import lombok.Data;

import java.util.List;

/**
 *
 * @author MSI MODERN 15 B13M
 */
@Data
public class LahanHijau {
    private String idLahan;
    
    private String luasLahan;
    private String latitude;
    private String longitude;
    private String[] filePath = new String[5];
    private Petugas petugasInput;
    private Peternak peternakInput;
    private Petugas petugasReview;
    private LahanStatus statusLahan;
    private String kecamatan;
    private String desa;
    private String kabupatenKota;
    private String provinsi;
    private String catatan;
    
    public LahanHijau(){
    }
    
    public static List<String> getAttributeNames(Class<?> clazz){
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName).collect(Collectors.toList());
    }

    
    public static class Builder{
        private final String latitude;
        private final String longitude;
        private String kecamatan = "";
        private String desa = "";
        private String kabupatenKota = "";
        private String provinsi = "";
        private String luasLahan = "0";
        private final LahanStatus statusLahan = LahanStatus.PENDING;
        private String catatan = "";
        public Builder(String latitude, String longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public Builder luasLahan(String luasLahan){
            this.luasLahan = luasLahan;
            return this;
        }
        
        public Builder catatan(String catatan){
            this.catatan = catatan;
            return this;
        }
        
        public LahanHijau build(){
            return new LahanHijau(this);
        }

        public Builder kecamatan(String kecamatan) {
            this.kecamatan = kecamatan;
            return this;
        }

        public Builder desa(String desa) {
            this.desa = desa;
            return this;
        }

        public Builder kabupatenKota(String kabupatenKota) {
            this.kabupatenKota = kabupatenKota;
            return this;
        }

        public Builder provinsi(String provinsi) {
            this.provinsi = provinsi;
            return this;
        }
        
    }

    public LahanHijau(Builder builder){
        this.catatan = builder.catatan;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.luasLahan = builder.luasLahan;
        this.statusLahan = builder.statusLahan;
        this.kecamatan = builder.kecamatan;
        this.desa = builder.desa;
        this.kabupatenKota = builder.kabupatenKota;
        this.provinsi = builder.provinsi;
    }

    
    
    
}

