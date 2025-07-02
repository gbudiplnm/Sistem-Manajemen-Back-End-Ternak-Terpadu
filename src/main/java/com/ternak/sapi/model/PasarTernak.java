/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ternak.sapi.model;

import com.ternak.sapi.payload.OpenHour;

import lombok.Data;

/**
 *
 * @author MSI MODERN 15 B13M
 */
@Data
public class PasarTernak {
    private String namaPasar;
    private String idPasar;
    private String latitude;
    private String[] filePath;
    private String longitude;
    private String desa;
    private String kecamatan;
    private String[] fungsiPasarHewan;
    private String[] hewanYangDijual;
    private String kabupatenKota;
    private String provinsi;
    private String catatan;
    private Petugas petugasPencatat;
    private OpenHour[] openHours;

    public PasarTernak() {
    }

    public static class Builder {
        private final String latitude;
        private final String longitude;
        private OpenHour [] openHours = new OpenHour[7];
        private String[] fungsiPasarHewan;
        private String[] hewanYangDijual;
        private String desa = "";
        private String kecamatan = "";
        private String kabupatenKota = "";
        private String provinsi = "";
        private String namaPasar = "";
        private String catatan = "";

        public Builder(String latitude, String longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Builder namaPasar(String namaPasar) {
            this.namaPasar = namaPasar;
            return this;
        }

        public Builder fungsiPasarHewan(String[] fungsiPasarHewan) {
            this.fungsiPasarHewan = fungsiPasarHewan;
            return this;
        }
        public Builder hewanYangDijual(String[] hewanYangDijual) {
            this.hewanYangDijual = hewanYangDijual;
            return this;
        }
        
        public Builder openHours(OpenHour[] openHours) {
            this.openHours = openHours;
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
            this.kabupatenKota  = kabupatenKota;
            return this;
        }
        public Builder provinsi(String provinsi) {
            this.provinsi = provinsi;
            return this;
        }

        public Builder catatan(String catatan) {
            this.catatan = catatan;
            return this;
        }

        public PasarTernak build() {
            return new PasarTernak(this);
        }

    }

    public PasarTernak(Builder builder){
        this.namaPasar = builder.namaPasar;
        this.openHours = builder.openHours;
        this.desa = builder.desa;
        this.fungsiPasarHewan = builder.fungsiPasarHewan;
        this.hewanYangDijual = builder.hewanYangDijual;
        this.kecamatan = builder.kecamatan;
        this.kabupatenKota = builder.kabupatenKota;
        this.provinsi = builder.provinsi;
        this.catatan = builder.catatan;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }
}
