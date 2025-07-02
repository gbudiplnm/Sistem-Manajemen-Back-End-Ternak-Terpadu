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
    private String desa;
    private String kecamatan;
    private String kabupatenKota;
    private String provinsi;
    private String idPuskesmas;
    private String catatan;
    private String[] dataLayanan;
    private String[] filePath;
    private String keterangan;
    private Petugas petugasPencatat;
    
    public Puskesmas() {
    }
    public static class Builder {
        private String keterangan;
        private String namaPuskesmas;
        private String longitude;
        private String[] dataLayanan;
        private String latitude;
        private String desa;
        private String kecamatan;
        private String kabupatenKota;
        private String provinsi;
        private String idPuskesmas;
        private String catatan;
        private String[] filePath;
        private Petugas petugasPencatat;

        public Builder(String namaPuskesmas, String longitude, String latitude) {
            this.namaPuskesmas = namaPuskesmas;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public Builder keterangan(String keterangan) {
            this.keterangan = keterangan;
            return this;
        }

        public Builder dataLayanan(String[] dataLayanan) {
            this.dataLayanan = dataLayanan;
            return this;
        }
        
        public Builder desa(String desa) {
            this.desa = desa;
            return this;
        }
        public Builder kecamatan(String kecamatan) {
            this.kecamatan = kecamatan;
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
        this.dataLayanan = builder.dataLayanan;
        this.desa = builder.desa;
        this.kecamatan = builder.kecamatan;
        this.kabupatenKota = builder.kabupatenKota;
        this.provinsi = builder.provinsi;
        this.idPuskesmas = builder.idPuskesmas;
        this.keterangan = builder.keterangan;
        this.catatan = builder.catatan;
        this.filePath = builder.filePath;
        this.petugasPencatat = builder.petugasPencatat;
    }


}
