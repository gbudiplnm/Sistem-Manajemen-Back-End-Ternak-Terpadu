/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ternak.sapi.model;

/**
 *
 * @author MSI MODERN 15 B13M
 */
public class PasarTernak {
    private String namaPasar;
    private String idPasar;
    private String latitude;
    private String longitude;
    private String alamatPasar;

    public void setAlamatPasar(String alamatPasar) {
        this.alamatPasar = alamatPasar;
    }

    public void setIdPasar(String idPasar) {
        this.idPasar = idPasar;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setNamaPasar(String namaPasar) {
        this.namaPasar = namaPasar;
    }

    public String getAlamatPasar() {
        return alamatPasar;
    }

    public String getIdPasar() {
        return idPasar;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getNamaPasar() {
        return namaPasar;
    }
}
