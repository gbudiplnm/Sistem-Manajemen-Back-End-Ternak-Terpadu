package com.ternak.sapi.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import enums.JenisHewanEnum;
import enums.LahanStatus;

@Data
public class LahanHijauRequest {
    @NotBlank(message = "namaLahanHijau is required")
    private String namaLahanHijau;
    @NotBlank(message = "jenisHewan is required")
    private JenisHewanEnum jenisHewan;
    @NotBlank(message = "luasLahan is required")
    private String luasLahan;
    @NotBlank(message = "latitude is required")
    private String latitude;
    @NotBlank(message = "longitude is required")
    private String longitude;
    @NotBlank(message = "filePath is required")
    private MultipartFile[] filePath;
    @NotBlank(message = "petugasInput is required")
    private String petugasInput;
    @NotBlank(message = "peternakInput is required")
    private String peternakInput;
    @NotBlank(message = "petugasReview is required")
    private String petugasReview;
    @NotBlank(message = "kecamatan is required")
    private String kecamatan;
    @NotBlank(message = "desa is required")
    private String desa;
    @NotBlank(message = "kabupatenKota is required")
    private String kabupatenKota;
    @NotBlank(message = "provinsi is required")
    private String provinsi;
    @NotBlank(message = "catatan is required")
    private String catatan;
}
