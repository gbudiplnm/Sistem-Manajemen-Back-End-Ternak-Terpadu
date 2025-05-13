package com.ternak.sapi.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.ternak.sapi.model.LahanHijau;
import com.ternak.sapi.model.Peternak;
import com.ternak.sapi.model.Petugas;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.LahanHijauRequest;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.queries.LahanHijauQuery;
import com.ternak.sapi.repository.LahanHijauRepository;
import com.ternak.sapi.util.AppUtility;

import enums.LahanStatus;

public class LahanHijauService {
    private LahanHijauRepository lahanHijauRepository = new LahanHijauRepository();
    private PeternakService peternakService = new PeternakService();
    private PetugasService petugasService = new PetugasService();

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
        LahanHijau lHijau = new LahanHijau.Builder(lahanHijau.getLongitude(), lahanHijau.getLatitude()).alamat(lahanHijau.getAlamat()).catatan(lahanHijau.getCatatan()).luasLahan(lahanHijau.getLuasLahan()).build();
        lHijau.setIdLahan(idLahan);
        lHijau.setFilePath(lahanHijau.getFilePath());
        lHijau.setStatusLahan(LahanStatus.DITERIMA);
        lHijau.setPetugasInput(petugas.getContent());
        lHijau.setPetugasReview(petugas.getContent());
        LahanHijau lahanHijauSave = lahanHijauRepository.save(lHijau);
        return lahanHijauSave;
    }
    
    public LahanHijau saveLahanHijauUser(LahanHijauRequest lahanHijau) throws IOException {
        String idLahan = java.util.UUID.randomUUID().toString();
        DefaultResponse<Peternak> peternak = peternakService.getPeternakById(lahanHijau.getPeternakInput());
        LahanHijau lHijau = new LahanHijau.Builder(lahanHijau.getLongitude(), lahanHijau.getLatitude()).alamat(lahanHijau.getAlamat()).catatan(lahanHijau.getCatatan()).luasLahan(lahanHijau.getLuasLahan()).build();
        lHijau.setIdLahan(idLahan);
        lHijau.setFilePath(lahanHijau.getFilePath());
        lHijau.setPeternakInput(peternak.getContent());
        LahanHijau lahanHijauSave = lahanHijauRepository.save(lHijau);
        return lahanHijauSave;
    }
    
    public LahanHijau getLahanHijauById(String id) throws IOException {
        LahanHijau lahanHijau = lahanHijauRepository.findLahanHijauById(id);
        return lahanHijau;
    }
    
    public LahanHijau updateLahanHijauAdmin(String id, LahanHijauRequest lahanHijauRequest) throws IOException {
        LahanHijau lahanHijau = lahanHijauRepository.findLahanHijauById(id);
        Petugas petugasReview = petugasService.getPetugasById(lahanHijauRequest.getPetugasReview()).getContent();
        if (lahanHijau != null) {
            lahanHijau.setAlamat(lahanHijauRequest.getAlamat());
            lahanHijau.setCatatan(lahanHijauRequest.getCatatan());
            lahanHijau.setFilePath(lahanHijau.getFilePath());
            lahanHijau.setStatusLahan(lahanHijauRequest.getStatusLahan());
            lahanHijauRepository.save(lahanHijau);
            if (lahanHijau.getStatusLahan() == LahanStatus.PENDING) {
                lahanHijau.setLuasLahan(lahanHijauRequest.getLuasLahan());
                lahanHijau.setPetugasReview(petugasReview);
            }
        }
        return lahanHijau;
    }
}
