package com.ternak.sapi.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.ternak.sapi.exception.ApiException;
import com.ternak.sapi.model.PasarTernak;
import com.ternak.sapi.model.Petugas;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PasarTernakEditRequest;
import com.ternak.sapi.payload.PasarTernakRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.repository.PasarTernakRepository;
import com.ternak.sapi.util.AppUtility;

public class PasarTernakService {

    private PasarTernakRepository pasarTernakRepository = new PasarTernakRepository();
    private PetugasService petugasService = new PetugasService();
    private HadoopFileService hadoopFileService = new HadoopFileService();

    public PagedResponse<PasarTernak> getAllPasarTernak(UniversalQueries query) throws IOException {
        AppUtility.validatePageNumberAndSize(query.getPage(), query.getSize());
        List<PasarTernak> pasarTernakList = pasarTernakRepository.findAll(query.getSize());
        return new PagedResponse<>(pasarTernakList, pasarTernakList.size(), "Successfully get data", 200);
    }

    public PasarTernak savePasarTernak(PasarTernakRequest pasarTernakRequest) throws IOException {
        boolean isAllFileHasCorrectExtension = AppUtility.isFileExtensionCorrect(pasarTernakRequest.getFilePath(),
                "jpg", "jpeg", "png");
        if (!isAllFileHasCorrectExtension) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File Tidak Sesuai, Hanya file gambar yang diizinkan");
        }
        String idPasar = java.util.UUID.randomUUID().toString();
        DefaultResponse<Petugas> petugas = petugasService.getPetugasById(pasarTernakRequest.getPetugasPencatat());
        List<String> path = new java.util.ArrayList<>();
        for (MultipartFile file : pasarTernakRequest.getFilePath()) {
            String idGambar = java.util.UUID.randomUUID().toString();
            String filePath = hadoopFileService.uploadFile(idGambar, file, "pasar-ternak");
            path.add(filePath);
        }
        String[] pathLists = Arrays.copyOf(path.toArray(), path.size(), String[].class);
        PasarTernak pasarTernak = new PasarTernak.Builder(pasarTernakRequest.getLatitude(),
                pasarTernakRequest.getLongitude()).desa(pasarTernakRequest.getDesa())
                .kabupatenKota(pasarTernakRequest.getKabupatenKota())
                .kecamatan(pasarTernakRequest.getKecamatan()).provinsi(pasarTernakRequest.getProvinsi())
                .namaPasar(pasarTernakRequest.getNamaPasar())
                .openHours(pasarTernakRequest.getOpenHours()).fungsiPasarHewan(pasarTernakRequest.getFungsiPasarHewan())
                .hewanYangDijual(pasarTernakRequest.getHewanYangDijual())
                .catatan(pasarTernakRequest.getCatatan()).build();
        pasarTernak.setIdPasar(idPasar);
        pasarTernak.setFilePath(pathLists);
        pasarTernak.setPetugasPencatat(petugas.getContent());
        PasarTernak pasarTernakSave = pasarTernakRepository.save(pasarTernak);
        return pasarTernakSave;
    }

    public PasarTernak getPasarTernakById(String id) throws IOException {
        PasarTernak pasarTernak = pasarTernakRepository.findPasarTernakById(id);
        return pasarTernak;
    }

    public PasarTernak updatePasarTernak(String id, PasarTernakEditRequest pasarTernakRequest) throws IOException {
        PasarTernak pasarTernak = pasarTernakRepository.findPasarTernakById(id);
        if (pasarTernak != null) {
            if (pasarTernakRequest.getNamaPasar() != null) {
                pasarTernak.setNamaPasar(pasarTernakRequest.getNamaPasar());
            }
            if (pasarTernakRequest.getLongitude() != null) {
                pasarTernak.setLongitude(pasarTernakRequest.getLongitude());
            }
            if (pasarTernakRequest.getLatitude() != null) {
                pasarTernak.setLatitude(pasarTernakRequest.getLatitude());
            }
            if (pasarTernakRequest.getDesa() != null) {
                pasarTernak.setDesa(pasarTernakRequest.getDesa());
            }
            if (pasarTernakRequest.getOpenHours() != null) {
                pasarTernak.setOpenHours(pasarTernakRequest.getOpenHours());
            }
            if (pasarTernakRequest.getFungsiPasarHewan() != null) {
                pasarTernak.setFungsiPasarHewan(pasarTernakRequest.getFungsiPasarHewan());
            }
            if (pasarTernakRequest.getHewanYangDijual() != null) {
                pasarTernak.setHewanYangDijual(pasarTernakRequest.getHewanYangDijual());
            }
            if (pasarTernakRequest.getKecamatan() != null) {
                pasarTernak.setKecamatan(pasarTernakRequest.getKecamatan());
            }
            if (pasarTernakRequest.getKabupatenKota() != null) {
                pasarTernak.setKabupatenKota(pasarTernakRequest.getKabupatenKota());
            }
            if (pasarTernakRequest.getProvinsi() != null) {
                pasarTernak.setProvinsi(pasarTernakRequest.getProvinsi());
            }
            if (pasarTernakRequest.getCatatan() != null) {
                pasarTernak.setCatatan(pasarTernakRequest.getCatatan());
            }
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND, "Data Pasar Ternak Tidak Ditemukan");
        }
        PasarTernak pasarTernakUpdated = pasarTernakRepository.save(pasarTernak);
        return pasarTernakUpdated;
    }

    public boolean deleteFile(String path, String id) throws IOException {
        try {
            PasarTernak pasarTernak = pasarTernakRepository.findPasarTernakById(id);
            String[] oldPaths = pasarTernak.getFilePath();
            String[] newPaths = new String[oldPaths.length - 1];
            for (String string : oldPaths) {
                if (!string.equalsIgnoreCase(path)) {
                    newPaths[newPaths.length - newPaths.length] = string;
                }
            }
            pasarTernak.setFilePath(newPaths);

            pasarTernakRepository.save(pasarTernak);
            return hadoopFileService.deleteFile(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(HttpStatus.NOT_FOUND, "Data Lahan Hijau Tidak Ditemukan");
        }
    }

    public String[] uploadFile(MultipartFile[] files, String id) throws IOException {
        String[] paths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String idGambar = java.util.UUID.randomUUID().toString();
            String filePath = hadoopFileService.uploadFile(idGambar, files[i], "pasar-ternak");
            paths[i] = filePath;
        }
        try {
            PasarTernak pasarTernak = pasarTernakRepository.findPasarTernakById(id);
            String[] oldPaths = pasarTernak.getFilePath();
            String[] newPaths = new String[oldPaths.length + paths.length];
            System.arraycopy(oldPaths, 0, newPaths, 0, oldPaths.length);
            System.arraycopy(paths, 0, newPaths, oldPaths.length, paths.length);
            pasarTernak.setFilePath(newPaths);
            pasarTernakRepository.save(pasarTernak);
        } catch (Exception e) {
            for (String filePath : paths) {
                hadoopFileService.deleteFile(filePath);
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Terjadi Kesalahan");
        }
        return paths;
    }

}
