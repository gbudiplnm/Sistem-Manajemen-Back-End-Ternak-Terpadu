/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ternak.sapi.model;

/**
 *
 * @author MSI MODERN 15 B13M
 */
public class PusatEvakuasi {
    private String idPusatEvakuasi;
    private String namaPusatEvakuasi;
    private String latitude;
    private String longitude;
    private String alamatPusatEvakuasi;

    public void setAlamatPusatEvakuasi(String alamatPusatEvakuasi) {
        this.alamatPusatEvakuasi = alamatPusatEvakuasi;
    }

    public void setIdPusatEvakuasi(String idPusatEvakuasi) {
        this.idPusatEvakuasi = idPusatEvakuasi;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setNamaPusatEvakuasi(String namaPusatEvakuasi) {
        this.namaPusatEvakuasi = namaPusatEvakuasi;
    }

    public String getAlamatPusatEvakuasi() {
        return alamatPusatEvakuasi;
    }

    public String getIdPusatEvakuasi() {
        return idPusatEvakuasi;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getNamaPusatEvakuasi() {
        return namaPusatEvakuasi;
    }       
}
