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
    private String desa;
    private String kecamatan;
    private String kabupatenKota;
    private String provinsi;
    private String catatan;
    private String[] filePath;
    private Petugas petugasPencatat;
    private String tempatEvakuasiBencana;
    private String jenisBencana;
    private String petaEvakuasi;
    private String jalurEvakuasi;

    public PusatEvakuasi() {
    }

    public static class Builder {
        private String tempatEvakuasiBencana;
        private String jenisBencana;
        private String petaEvakuasi;
        private String jalurEvakuasi;
        private String idPusatEvakuasi;
        private String namaPusatEvakuasi;
        private String latitude;
        private String longitude;
        private String desa;
        private String kecamatan;
        private String kabupatenKota;
        private String provinsi;
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

        public Builder petaEvakuasi(String petaEvakuasi) {
            this.petaEvakuasi = petaEvakuasi;
            return this;
        }
        public Builder jalurEvakuasi(String jalurEvakuasi) {
            this.jalurEvakuasi = jalurEvakuasi;
            return this;
        }
        public Builder jenisBencana(String jenisBencana) {
            this.jenisBencana = jenisBencana;
            return this;
        }
        public Builder tempatEvakuasiBencana(String tempatEvakuasiBencana) {
            this.tempatEvakuasiBencana = tempatEvakuasiBencana;
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
        this.tempatEvakuasiBencana = builder.tempatEvakuasiBencana;
        this.jenisBencana = builder.jenisBencana;
        this.petaEvakuasi = builder.petaEvakuasi;
        this.jalurEvakuasi = builder.jalurEvakuasi;
        this.namaPusatEvakuasi = builder.namaPusatEvakuasi;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.desa = builder.desa;
        this.kecamatan = builder.kecamatan;
        this.kabupatenKota = builder.kabupatenKota;
        this.provinsi = builder.provinsi;
        this.catatan = builder.catatan;
        this.filePath = builder.filePath;
        this.petugasPencatat = builder.petugasPencatat;
    }
}
