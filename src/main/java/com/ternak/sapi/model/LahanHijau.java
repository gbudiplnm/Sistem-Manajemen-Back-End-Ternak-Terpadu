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
    private String alamat;
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
        private String luasLahan = "0";
        private final LahanStatus statusLahan = LahanStatus.PENDING;
        private String alamat = "";
        private String catatan = "";
        public Builder(String latitude, String longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public Builder luasLahan(String luasLahan){
            this.luasLahan = luasLahan;
            return this;
        }
        
        public Builder alamat(String alamat){
            this.alamat = alamat;
            return this;
        }
        
        public Builder catatan(String catatan){
            this.catatan = catatan;
            return this;
        }
        
        public LahanHijau build(){
            return new LahanHijau(this);
        }
        
    }

    public LahanHijau(Builder builder){
        this.alamat = builder.alamat;
        this.catatan = builder.catatan;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.luasLahan = builder.luasLahan;
        this.statusLahan = builder.statusLahan;
    }

    
    
    
}

