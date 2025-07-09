package com.ternak.sapi.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
            HadoopFileService hadoopFileService) {
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
        Petugas petugas;
        try {            
            DefaultResponse<Petugas> resp = petugasService.getPetugasById(lahanHijau.getPetugasInput());
            petugas = resp.getContent();
        } catch (Exception e) {
            User user;
            try {
                user = userService.getUserById(lahanHijau.getPetugasInput());
            } catch (Exception er) {
                er.printStackTrace();
                throw new ApiException(HttpStatus.BAD_REQUEST, "User Tidak Ditemukan");
            }
            petugas = new Petugas(user.getId(), user.getNik(), user.getName(), "0", user.getEmail(), "","");
        }
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
                    .kepemilikan(lahanHijau.getKepemilikan())
                    .catatan(lahanHijau.getCatatan()).luasLahan(lahanHijau.getLuasLahan())
                    .jenisHewan(lahanHijau.getJenisHewan())
                    .namaLahanHijau(lahanHijau.getNamaLahanHijau())
                    .build();
            lHijau.setIdLahan(idLahan);
            lHijau.setFilePath(path);
            lHijau.setStatusLahan(LahanStatus.DITERIMA);
            lHijau.setPetugasInput(petugas);
            lHijau.setPetugasReview(petugas);
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
        Peternak peternak;
        try {
            DefaultResponse<Peternak> resp = peternakService.getPeternakById(lahanHijau.getPeternakInput());
            peternak = resp.getContent();
        } catch (Exception e) {
            User user;
            try {
                user = userService.getUserById(lahanHijau.getPeternakInput());
            } catch (Exception er) {
                er.printStackTrace();
                throw new ApiException(HttpStatus.BAD_REQUEST, "User Tidak Ditemukan");
            }
            peternak = new Peternak(user.getId(), user.getNik(), user.getName(), "0", null, "","",user.getEmail(),"Laki-Laki","0-0-0000","0","","","","",user.getAlamat(),"","","");

        }
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
                    .kepemilikan(lahanHijau.getKepemilikan())
                    .catatan(lahanHijau.getCatatan()).luasLahan(lahanHijau.getLuasLahan())
                    .jenisHewan(lahanHijau.getJenisHewan())
                    .namaLahanHijau(lahanHijau.getNamaLahanHijau())
                    .build();
            lHijau.setIdLahan(idLahan);
            lHijau.setFilePath(path);
            lHijau.setStatusLahan(LahanStatus.PENDING);
            lHijau.setPeternakInput(peternak);
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
            if (lahanHijauRequest.getJenisHewan() != null) {
                lahanHijau.setJenisHewan(lahanHijauRequest.getJenisHewan());
            }
            if (lahanHijauRequest.getProvinsi() != null) {
                lahanHijau.setProvinsi(lahanHijauRequest.getProvinsi());
            }
            if (lahanHijauRequest.getKabupatenKota() != null) {
                lahanHijau.setKabupatenKota(lahanHijauRequest.getKabupatenKota());
            }
            if (lahanHijauRequest.getKecamatan() != null) {
                lahanHijau.setKecamatan(lahanHijauRequest.getKecamatan());
            }
            if (lahanHijauRequest.getLongitude() != null) {
                lahanHijau.setLongitude(lahanHijauRequest.getLongitude());
            }
            if (lahanHijauRequest.getLatitude() != null) {
                lahanHijau.setLatitude(lahanHijauRequest.getLatitude());
            }
            if (lahanHijauRequest.getNamaLahanHijau() != null) {
                lahanHijau.setNamaLahanHijau(lahanHijauRequest.getNamaLahanHijau());
            }
            if (lahanHijauRequest.getDesa() != null) {
                lahanHijau.setDesa(lahanHijauRequest.getDesa());
            }
            if (lahanHijauRequest.getCatatan() != null) {
                lahanHijau.setCatatan(lahanHijauRequest.getCatatan());
            }
            if (lahanHijauRequest.getLuasLahan() != null) {
                lahanHijau.setLuasLahan(lahanHijauRequest.getLuasLahan());
            }
            if (lahanHijauRequest.getKepemilikan() != null) {
                lahanHijau.setKepemilikan(lahanHijauRequest.getKepemilikan());
            }
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
        Petugas petugas;
        try {            
            petugas = petugasService.getPetugasByNik(user.getNik()).getContent();
        } catch (Exception e) {
            petugas = new Petugas(user.getId(), user.getNik(), user.getName(), "0", user.getEmail(), "","");
        }
        if (lahanHijau != null) {
            lahanHijau.setStatusLahan(LahanStatus.DITERIMA);
            lahanHijau.setCatatan(Catatan);
            lahanHijau.setPetugasReview(petugas);
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
        Petugas petugas;
        try {            
            petugas = petugasService.getPetugasByNik(user.getNik()).getContent();
        } catch (Exception e) {
            petugas = new Petugas(user.getId(), user.getNik(), user.getName(), "0", user.getEmail(), "","");
        }        if (lahanHijau != null) {
            lahanHijau.setStatusLahan(LahanStatus.DITOLAK);
            lahanHijau.setCatatan(Catatan);
            lahanHijau.setPetugasReview(petugas);
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
