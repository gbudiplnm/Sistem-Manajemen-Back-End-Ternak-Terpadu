package com.ternak.sapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ternak.sapi.exception.ApiException;
import com.ternak.sapi.model.LahanHijau;
import com.ternak.sapi.model.Peternak;
import com.ternak.sapi.model.Petugas;
import com.ternak.sapi.model.User;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.LahanHijauEditRequest;
import com.ternak.sapi.payload.LahanHijauRequest;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.UserSummary;
import com.ternak.sapi.queries.LahanHijauQuery;
import com.ternak.sapi.repository.LahanHijauRepository;
import com.ternak.sapi.util.AppUtility;

import enums.LahanStatus;

@Service
public class LahanHijauService {


    private final LahanHijauRepository lahanHijauRepository;
    private final PeternakService peternakService;
    private final PetugasService petugasService;
    private final UserService userService;
    private final HadoopFileService hadoopFileService;

    @Autowired(required = true)
    public LahanHijauService(
            LahanHijauRepository lahanHijauRepository,
            PeternakService peternakService,
            PetugasService petugasService,
            UserService userService,
            HadoopFileService hadoopFileService
    ) {
        this.lahanHijauRepository = lahanHijauRepository;
        this.peternakService = peternakService;
        this.petugasService = petugasService;
        this.userService = userService;
        this.hadoopFileService = hadoopFileService;
    }

    public PagedResponse<LahanHijau> getAllLahanHijau(LahanHijauQuery query) throws IOException {
        AppUtility.validatePageNumberAndSize(query.getPage(), query.getSize());
        List<LahanHijau> lahanHijauList = lahanHijauRepository.findAll(query.getSize());
        return new PagedResponse<>(lahanHijauList, lahanHijauList.size(), "Successfully get data", 200);
    }

    public PagedResponse<LahanHijau> getAllLahanHijauUser(LahanHijauQuery query) throws IOException {
        AppUtility.validatePageNumberAndSize(query.getPage(), query.getSize());
        List<LahanHijau> lahanHijauList = lahanHijauRepository.findAllUser(query.getSize());
        return new PagedResponse<>(lahanHijauList, lahanHijauList.size(), "Successfully get data", 200);
    }

    public LahanHijau saveLahanHijauAdmin(LahanHijauRequest lahanHijau) throws IOException {
        String idLahan = java.util.UUID.randomUUID().toString();
        DefaultResponse<Petugas> petugas = petugasService.getPetugasById(lahanHijau.getPetugasInput());
        List<String> pathLists = new java.util.ArrayList<>();
        for (MultipartFile file : lahanHijau.getFilePath()) {
            String idGambar = java.util.UUID.randomUUID().toString();
            String filePath = hadoopFileService.uploadFile(idGambar, file, "lahan-hijau");
            pathLists.add(filePath);
        }
        String[] path = Arrays.copyOf(pathLists.toArray(), pathLists.size(), String[].class);
        try {
            LahanHijau lHijau = new LahanHijau.Builder(lahanHijau.getLatitude(), lahanHijau.getLongitude())
                    .provinsi(lahanHijau.getProvinsi()).kabupatenKota(lahanHijau.getKabupatenKota())
                    .kecamatan(lahanHijau.getKecamatan()).desa(lahanHijau.getDesa())
                    .catatan(lahanHijau.getCatatan()).luasLahan(lahanHijau.getLuasLahan())
                    .jenisHewan(lahanHijau.getJenisHewan())
                    .namaLahanHijau(lahanHijau.getNamaLahanHijau())
                    .build();
            lHijau.setIdLahan(idLahan);
            lHijau.setFilePath(path);
            lHijau.setStatusLahan(LahanStatus.DITERIMA);
            lHijau.setPetugasInput(petugas.getContent());
            lHijau.setPetugasReview(petugas.getContent());
            LahanHijau lahanHijauSave = lahanHijauRepository.save(lHijau);
            return lahanHijauSave;
        } catch (Exception e) {
            for (String filePath : path) {
                hadoopFileService.deleteFile(filePath);
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Lahan Hijau Sudah Di Terima");
        }
    }

    public LahanHijau saveLahanHijauUser(LahanHijauRequest lahanHijau) throws IOException {
        String idLahan = java.util.UUID.randomUUID().toString();
        DefaultResponse<Peternak> peternak = peternakService.getPeternakById(lahanHijau.getPeternakInput());
        List<String> pathLists = new java.util.ArrayList<>();
        for (MultipartFile file : lahanHijau.getFilePath()) {
            String idGambar = java.util.UUID.randomUUID().toString();
            String filePath = hadoopFileService.uploadFile(idGambar, file, "lahan-hijau");
            pathLists.add(filePath);
        }
        String[] path = Arrays.copyOf(pathLists.toArray(), pathLists.size(), String[].class);
        try {
            LahanHijau lHijau = new LahanHijau.Builder(lahanHijau.getLatitude(), lahanHijau.getLongitude())
                    .provinsi(lahanHijau.getProvinsi()).kabupatenKota(lahanHijau.getKabupatenKota())
                    .kecamatan(lahanHijau.getKecamatan()).desa(lahanHijau.getDesa())
                    .catatan(lahanHijau.getCatatan()).luasLahan(lahanHijau.getLuasLahan())
                    .jenisHewan(lahanHijau.getJenisHewan())
                    .namaLahanHijau(lahanHijau.getNamaLahanHijau())
                    .build();
            lHijau.setIdLahan(idLahan);
            lHijau.setFilePath(path);
            lHijau.setStatusLahan(LahanStatus.PENDING);
            lHijau.setPeternakInput(peternak.getContent());
            LahanHijau lahanHijauSave = lahanHijauRepository.save(lHijau);
            return lahanHijauSave;
        } catch (Exception e) {
            for (String filePath : path) {
                hadoopFileService.deleteFile(filePath);
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Lahan Hijau Sudah Di Terima");
        }
    }

    public LahanHijau getLahanHijauById(String id) throws IOException {
        LahanHijau lahanHijau = lahanHijauRepository.findLahanHijauById(id);
        return lahanHijau;
    }

    public LahanHijau updateLahanHijauAdmin(String id, LahanHijauEditRequest lahanHijauRequest) throws IOException {
        LahanHijau lahanHijau = lahanHijauRepository.findLahanHijauById(id);
        if (lahanHijau != null) {
            lahanHijau.setJenisHewan(lahanHijauRequest.getJenisHewan());
            lahanHijau.setProvinsi(lahanHijauRequest.getProvinsi());
            lahanHijau.setKabupatenKota(lahanHijauRequest.getKabupatenKota());
            lahanHijau.setKecamatan(lahanHijauRequest.getKecamatan());
            lahanHijau.setLongitude(lahanHijauRequest.getLongitude());
            lahanHijau.setLatitude(lahanHijauRequest.getLatitude());
            lahanHijau.setNamaLahanHijau(lahanHijauRequest.getNamaLahanHijau());
            lahanHijau.setDesa(lahanHijauRequest.getDesa());
            lahanHijau.setCatatan(lahanHijauRequest.getCatatan());
            lahanHijau.setLuasLahan(lahanHijauRequest.getLuasLahan());
            lahanHijauRepository.save(lahanHijau);
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND, "Data Lahan Hijau Tidak Ditemukan");
        }
        return lahanHijau;
    }

    public LahanHijau terimaLahanHijauPetugas(String id, UserSummary userSummary, String Catatan) throws IOException {
        LahanHijau lahanHijau = lahanHijauRepository.findLahanHijauById(id);
        if (lahanHijau.getStatusLahan() != LahanStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Lahan Hijau Sudah Di Terima");
        }
        ;
        User user = userService.getUserById(userSummary.getId());
        Petugas petugasReview = petugasService.getPetugasByNik(user.getNik()).getContent();
        if (lahanHijau != null) {
            lahanHijau.setStatusLahan(LahanStatus.DITERIMA);
            lahanHijau.setCatatan(Catatan);
            lahanHijau.setPetugasReview(petugasReview);
            lahanHijauRepository.save(lahanHijau);
        }
        return lahanHijau;
    }

    public LahanHijau tolakLahanHijauPetugas(String id, UserSummary userSummary, String Catatan) throws IOException {
        LahanHijau lahanHijau = lahanHijauRepository.findLahanHijauById(id);
        if (lahanHijau.getStatusLahan() != LahanStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Lahan Hijau Sudah Di Tolak");
        }
        ;
        User user = userService.getUserById(userSummary.getId());
        Petugas petugasReview = petugasService.getPetugasByNik(user.getNik()).getContent();
        if (lahanHijau != null) {
            lahanHijau.setStatusLahan(LahanStatus.DITOLAK);
            lahanHijau.setCatatan(Catatan);
            lahanHijau.setPetugasReview(petugasReview);
            lahanHijauRepository.save(lahanHijau);
        }
        return lahanHijau;
    }

    public boolean deleteFile(String path, String id) throws IOException {
        try {
            LahanHijau lahanHijau = lahanHijauRepository.findLahanHijauById(id);
            String[] oldPaths = lahanHijau.getFilePath();
            String[] newPaths = new String[oldPaths.length - 1];
            for (String string : oldPaths) {
                if (!string.equalsIgnoreCase(path)) {
                    newPaths[newPaths.length - newPaths.length] = string;
                }
            }
            lahanHijau.setFilePath(newPaths);

            lahanHijauRepository.save(lahanHijau);
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
            String filePath = hadoopFileService.uploadFile(idGambar, files[i], "lahan-hijau");
            paths[i] = filePath;
        }
        try {
            LahanHijau lahanHijau = lahanHijauRepository.findLahanHijauById(id);
            String[] oldPaths = lahanHijau.getFilePath();
            String[] newPaths = new String[oldPaths.length + paths.length];
            System.arraycopy(oldPaths, 0, newPaths, 0, oldPaths.length);
            System.arraycopy(paths, 0, newPaths, oldPaths.length, paths.length);
            lahanHijau.setFilePath(newPaths);
            lahanHijauRepository.save(lahanHijau);
        } catch (Exception e) {
            for (String filePath : paths) {
                hadoopFileService.deleteFile(filePath);
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Terjadi Kesalahan");
        }
        return paths;
    }

}
