package com.ternak.sapi.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.ternak.sapi.model.Puskesmas;
import com.ternak.sapi.exception.ApiException;
import com.ternak.sapi.model.Petugas;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PuskesmasEditRequest;
import com.ternak.sapi.payload.PuskesmasRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.repository.PuskesmasRepository;
import com.ternak.sapi.util.AppUtility;

public class PuskesmasService {

    private PuskesmasRepository puskesmasRepository = new PuskesmasRepository();
    private PetugasService petugasService = new PetugasService();
    private HadoopFileService hadoopFileService = new HadoopFileService();

    public PagedResponse<Puskesmas> getAllPuskesmas(UniversalQueries query) throws IOException {
        AppUtility.validatePageNumberAndSize(query.getPage(), query.getSize());
        List<Puskesmas> puskesmasList = puskesmasRepository.findAll(query.getSize());
        return new PagedResponse<>(puskesmasList, puskesmasList.size(), "Successfully get data", 200);
    }

    public Puskesmas savePuskesmas(PuskesmasRequest puskesmasRequest) throws IOException {
        String idPuskesmas = java.util.UUID.randomUUID().toString();
        DefaultResponse<Petugas> petugas = petugasService.getPetugasById(puskesmasRequest.getPetugasPencatat());
        List<String> pathLists = new java.util.ArrayList<>();
        for (MultipartFile file : puskesmasRequest.getFilePath()) {
            String idGambar = java.util.UUID.randomUUID().toString();
            String filePath = hadoopFileService.uploadFile(idGambar, file, "puskesmas");
            pathLists.add(filePath);
        }
        String[] path = Arrays.copyOf(pathLists.toArray(), pathLists.size(), String[].class);
        Puskesmas puskesmas = new Puskesmas.Builder(puskesmasRequest.getNamaPuskesmas(),
                puskesmasRequest.getLongitude(), puskesmasRequest.getLatitude())
                .desa(puskesmasRequest.getDesa())
                .dataLayanan(puskesmasRequest.getDataLayanan())
                .keterangan(puskesmasRequest.getKeterangan())
                .kecamatan(puskesmasRequest.getKecamatan())
                .kabupatenKota(puskesmasRequest.getKabupatenKota())
                .provinsi(puskesmasRequest.getProvinsi())
                .catatan(puskesmasRequest.getCatatan())
                .idPuskesmas(idPuskesmas)
                .filePath(path)
                .petugasPencatat(petugas.getContent())
                .build();
        Puskesmas puskesmasSave = puskesmasRepository.save(puskesmas);
        return puskesmasSave;
    }

    public Puskesmas getPuskesmasById(String id) throws IOException {
        Puskesmas puskesmas = puskesmasRepository.findPuskesmasById(id);
        return puskesmas;
    }

    public Puskesmas updatePuskesmas(String id, PuskesmasEditRequest puskesmasRequest) throws IOException {
        Puskesmas puskesmas = puskesmasRepository.findPuskesmasById(id);
        if (puskesmas != null) {
            if (puskesmasRequest.getNamaPuskesmas() != null) {
                puskesmas.setNamaPuskesmas(puskesmasRequest.getNamaPuskesmas());
            }
            if (puskesmasRequest.getLongitude() != null) {
                puskesmas.setLongitude(puskesmasRequest.getLongitude());
            }
            if (puskesmasRequest.getLatitude() != null) {
                puskesmas.setLatitude(puskesmasRequest.getLatitude());
            }
            if (puskesmasRequest.getDesa() != null) {
                puskesmas.setDesa(puskesmasRequest.getDesa());
            }
            if (puskesmasRequest.getKecamatan() != null) {
                puskesmas.setKecamatan(puskesmasRequest.getKecamatan());
            }
            if (puskesmasRequest.getKabupatenKota() != null) {
                puskesmas.setKabupatenKota(puskesmasRequest.getKabupatenKota());
            }
            if (puskesmasRequest.getProvinsi() != null) {
                puskesmas.setProvinsi(puskesmasRequest.getProvinsi());
            }
            if (puskesmasRequest.getCatatan() != null) {
                puskesmas.setCatatan(puskesmasRequest.getCatatan());
            }
            if (puskesmasRequest.getKeterangan() != null) {
                puskesmas.setKeterangan(puskesmasRequest.getKeterangan());
            }
            if (puskesmasRequest.getDataLayanan() != null) {
                puskesmas.setDataLayanan(puskesmasRequest.getDataLayanan());
            }
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND, "Data Puskesmas Tidak Ditemukan");
        }
        Puskesmas puskesmasUpdated = puskesmasRepository.save(puskesmas);
        return puskesmasUpdated;
    }

    public boolean deleteFile(String path, String id) throws IOException {
        try {
            Puskesmas puskesmas = puskesmasRepository.findPuskesmasById(id);
            String[] oldPaths = puskesmas.getFilePath();
            String[] newPaths = new String[oldPaths.length - 1];
            for (String string : oldPaths) {
                if (!string.equalsIgnoreCase(path)) {
                    newPaths[newPaths.length - newPaths.length] = string;
                }
            }
            puskesmas.setFilePath(newPaths);

            puskesmasRepository.save(puskesmas);
            return hadoopFileService.deleteFile(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(HttpStatus.NOT_FOUND, "Data Puskesmas Tidak Ditemukan");
        }
    }

    public String[] uploadFile(MultipartFile[] files, String id) throws IOException {
        String[] paths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String idGambar = java.util.UUID.randomUUID().toString();
            String filePath = hadoopFileService.uploadFile(idGambar, files[i], "puskesmas");
            paths[i] = filePath;
        }
        try {
            Puskesmas puskesmas = puskesmasRepository.findPuskesmasById(id);
            String[] oldPaths = puskesmas.getFilePath();
            String[] newPaths = new String[oldPaths.length + paths.length];
            System.arraycopy(oldPaths, 0, newPaths, 0, oldPaths.length);
            System.arraycopy(paths, 0, newPaths, oldPaths.length, paths.length);
            puskesmas.setFilePath(newPaths);
            puskesmasRepository.save(puskesmas);
        } catch (Exception e) {
            for (String filePath : paths) {
                hadoopFileService.deleteFile(filePath);
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Terjadi Kesalahan");
        }
        return paths;
    }

}
