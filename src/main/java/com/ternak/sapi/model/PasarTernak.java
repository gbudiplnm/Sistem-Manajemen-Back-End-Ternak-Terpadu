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
public class PasarTernak {
    private String namaPasar;
    private String idPasar;
    private String latitude;
    private String[] filePath = new String[5];
    private String longitude;
    private String alamatPasar;
    private String catatan;
    private Petugas petugasPencatat;

    public PasarTernak() {
    }

    public static class Builder {
        private final String latitude;
        private final String longitude;
        private String alamat = "";
        private String catatan = "";

        public Builder(String latitude, String longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }


        public Builder alamat(String alamat) {
            this.alamat = alamat;
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
        this.alamatPasar = builder.alamat;
        this.catatan = builder.catatan;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }
}
