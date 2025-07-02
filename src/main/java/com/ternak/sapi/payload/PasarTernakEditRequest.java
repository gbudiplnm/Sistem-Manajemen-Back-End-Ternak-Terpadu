package com.ternak.sapi.payload;


import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PasarTernakEditRequest {
    private String namaPasar;
    private String latitude;
    private String longitude;    
    private String[] fungsiPasarHewan;
    private String[] hewanYangDijual;
    private OpenHour [] openHours;
    private MultipartFile[] filePath;
    private String catatan;
    private String desa;
    private String kecamatan;
    private String kabupatenKota;
    private String provinsi;
}
