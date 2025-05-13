package com.ternak.sapi.service;

import java.io.IOException;
import java.util.List;

import com.ternak.sapi.model.PasarTernak;
import com.ternak.sapi.model.Petugas;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PasarTernakRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.repository.PasarTernakRepository;
import com.ternak.sapi.util.AppUtility;

public class PasarTernakService {

    private PasarTernakRepository pasarTernakRepository = new PasarTernakRepository();
    private PetugasService petugasService = new PetugasService();

    public PagedResponse<PasarTernak> getAllPasarTernak(UniversalQueries query) throws IOException {
        AppUtility.validatePageNumberAndSize(query.getPage(), query.getSize());
        List<PasarTernak> pasarTernakList = pasarTernakRepository.findAll(query.getSize());
        return new PagedResponse<>(pasarTernakList, pasarTernakList.size(), "Successfully get data", 200);
    }

    public PasarTernak savePasarTernak(PasarTernakRequest pasarTernakRequest) throws IOException {
        String idPasar = java.util.UUID.randomUUID().toString();
        DefaultResponse<Petugas> petugas = petugasService.getPetugasById(pasarTernakRequest.getPetugasPencatat());
        PasarTernak pasarTernak = new PasarTernak.Builder(pasarTernakRequest.getLongitude(),
                pasarTernakRequest.getLatitude()).alamat(pasarTernakRequest.getAlamatPasar())
                .catatan(pasarTernakRequest.getCatatan()).build();
        pasarTernak.setIdPasar(idPasar);
        pasarTernak.setFilePath(pasarTernakRequest.getFilePath());
        pasarTernak.setPetugasPencatat(petugas.getContent());
        PasarTernak pasarTernakSave = pasarTernakRepository.save(pasarTernak);
        return pasarTernakSave;
    }

    public PasarTernak getPasarTernakById(String id) throws IOException {
        PasarTernak pasarTernak = pasarTernakRepository.findPasarTernakById(id);
        return pasarTernak;
    }

    public PasarTernak updatePasarTernak(String id, PasarTernakRequest pasarTernakRequest) throws IOException {
        PasarTernak pasarTernak = pasarTernakRepository.findPasarTernakById(id);
        Petugas petugasReview = petugasService.getPetugasById(pasarTernakRequest.getPetugasPencatat()).getContent();
        if (pasarTernak != null) {
            pasarTernak.setAlamatPasar(pasarTernakRequest.getAlamatPasar());
            pasarTernak.setCatatan(pasarTernakRequest.getCatatan());
            pasarTernak.setFilePath(pasarTernakRequest.getFilePath());
            pasarTernak.setPetugasPencatat(petugasReview);
        }
        PasarTernak pasarTernakUpdated = pasarTernakRepository.save(pasarTernak);
        return pasarTernakUpdated;
    }
}
