package com.ternak.sapi.payload;


import enums.JenisHewanEnum;
import enums.KepemilikanEnum;
import lombok.Data;

@Data
public class LahanHijauEditRequest {
    private String luasLahan;
    private String latitude;
    private String longitude;
    private String kecamatan;
    private String namaLahanHijau;
    private KepemilikanEnum kepemilikan;
    private JenisHewanEnum jenisHewan;
    private String desa;
    private String kabupatenKota;
    private String provinsi;
    private String catatan;
}
