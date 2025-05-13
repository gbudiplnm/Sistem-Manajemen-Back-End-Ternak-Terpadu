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
public class PusatEvakuasi {
    private String idPusatEvakuasi;
    private String namaPusatEvakuasi;
    private String latitude;
    private String longitude;
    private String alamatPusatEvakuasi;
    private String catatan;
    private String[] filePath = new String[5];
    private Petugas petugasPencatat;   

    public PusatEvakuasi() {
    }
    
    public static class Builder {
        private String idPusatEvakuasi;
        private String namaPusatEvakuasi;
        private String latitude;
        private String longitude;
        private String alamatPusatEvakuasi;
        private String catatan;
        private String[] filePath = new String[5];
        private Petugas petugasPencatat;
        
        public Builder idPusatEvakuasi(String idPusatEvakuasi) {
            this.idPusatEvakuasi = idPusatEvakuasi;
            return this;
        }
        
        public Builder namaPusatEvakuasi(String namaPusatEvakuasi) {
            this.namaPusatEvakuasi = namaPusatEvakuasi;
            return this;
        }

        public Builder(String longitude, String latitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        public Builder alamatPusatEvakuasi(String alamatPusatEvakuasi) {
            this.alamatPusatEvakuasi = alamatPusatEvakuasi;
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
        
        public PusatEvakuasi build() {
            return new PusatEvakuasi(this);
        }
    }
    
    public PusatEvakuasi(Builder builder) {
        this.idPusatEvakuasi = builder.idPusatEvakuasi;
        this.namaPusatEvakuasi = builder.namaPusatEvakuasi;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.alamatPusatEvakuasi = builder.alamatPusatEvakuasi;
        this.catatan = builder.catatan;
        this.filePath = builder.filePath;
        this.petugasPencatat = builder.petugasPencatat;
    }
}
