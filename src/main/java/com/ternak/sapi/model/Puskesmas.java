/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ternak.sapi.model;

import lombok.Data;

/**
 *
 * @author MSI MODERN 15 B13M
 */
@Data
public class Puskesmas {
    
    private String namaPuskesmas;
    private String longitude;
    private String latitude;
    private String alamat;
    private String idPuskesmas;
    private String catatan;
    private String[] filePath = new String[5];
    private Petugas petugasPencatat;
    
    public Puskesmas() {
    }
    public static class Builder {
        private String namaPuskesmas;
        private String longitude;
        private String latitude;
        private String alamat;
        private String idPuskesmas;
        private String catatan;
        private String[] filePath = new String[5];
        private Petugas petugasPencatat;

        public Builder(String namaPuskesmas, String longitude, String latitude) {
            this.namaPuskesmas = namaPuskesmas;
            this.longitude = longitude;
            this.latitude = latitude;
        }
        
        public Builder alamat(String alamat) {
            this.alamat = alamat;
            return this;
        }
        
        public Builder idPuskesmas(String idPuskesmas) {
            this.idPuskesmas = idPuskesmas;
            return this;
        }
        
        public Builder catatan(String catatan) {
            this.catatan = catatan;
            return this;
        }
        
        public Builder filePath(String[] filePath) {
            this.filePath = filePath;
            return this;
        }
        
        public Builder petugasPencatat(Petugas petugasPencatat) {
            this.petugasPencatat = petugasPencatat;
            return this;
        }
        
        public Puskesmas build() {
            return new Puskesmas(this);
        }
        
    }
    public Puskesmas(Builder builder) {
        this.namaPuskesmas = builder.namaPuskesmas;
        this.longitude = builder.longitude;
        this.latitude = builder.latitude;
        this.alamat = builder.alamat;
        this.idPuskesmas = builder.idPuskesmas;
        this.catatan = builder.catatan;
        this.filePath = builder.filePath;
        this.petugasPencatat = builder.petugasPencatat;
    }


}
