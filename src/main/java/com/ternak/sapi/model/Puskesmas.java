/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ternak.sapi.model;

/**
 *
 * @author MSI MODERN 15 B13M
 */
public class Puskesmas {
    
    private String namaPuskesmas;
    private String longitude;
    private String latitude;
    private String alamat;
    private String idPuskesmas;

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setIdPuskesmas(String idPuskesmas) {
        this.idPuskesmas = idPuskesmas;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setNamaPuskesmas(String namaPuskesmas) {
        this.namaPuskesmas = namaPuskesmas;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getIdPuskesmas() {
        return idPuskesmas;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getNamaPuskesmas() {
        return namaPuskesmas;
    }
    
}
