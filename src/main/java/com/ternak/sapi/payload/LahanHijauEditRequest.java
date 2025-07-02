package com.ternak.sapi.payload;

import javax.validation.constraints.NotBlank;

import enums.JenisHewanEnum;
import lombok.Data;

@Data
public class LahanHijauEditRequest {
    @NotBlank(message = "luasLahan field is required")
    private String luasLahan;
    @NotBlank(message = "latitude field is required")
    private String latitude;
    @NotBlank(message = "longitude field is required")
    private String longitude;
    @NotBlank(message = "kecamatan field is required")
    private String kecamatan;
    @NotBlank(message = "namaLahanHijau field is required")
    private String namaLahanHijau;
    @NotBlank(message = "jenisHewan field is required")
    private JenisHewanEnum jenisHewan;
    @NotBlank(message = "desa field is required")
    private String desa;
    @NotBlank(message = "kabupatenKota field is required")
    private String kabupatenKota;
    @NotBlank(message = "provinsi field is required")
    private String provinsi;
    @NotBlank(message = "catatan field is required")
    private String catatan;
}
