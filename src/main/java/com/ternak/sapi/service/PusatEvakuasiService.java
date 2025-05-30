package com.ternak.sapi.service;

import java.io.IOException;
import java.util.List;

import com.ternak.sapi.model.PusatEvakuasi;
import com.ternak.sapi.model.Petugas;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PusatEvakuasiRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.repository.PusatEvakuasiRepository;
import com.ternak.sapi.util.AppUtility;

public class PusatEvakuasiService {

    private PusatEvakuasiRepository pusatEvakuasiRepository = new PusatEvakuasiRepository();
    private PetugasService petugasService = new PetugasService();

    public PagedResponse<PusatEvakuasi> getAllPusatEvakuasi(UniversalQueries query) throws IOException {
        AppUtility.validatePageNumberAndSize(query.getPage(), query.getSize());
        List<PusatEvakuasi> pusatEvakuasiList = pusatEvakuasiRepository.findAll(query.getSize());
        return new PagedResponse<>(pusatEvakuasiList, pusatEvakuasiList.size(), "Successfully get data", 200);
    }

    public PusatEvakuasi savePusatEvakuasi(PusatEvakuasiRequest pusatEvakuasiRequest) throws IOException {
        String idPasar = java.util.UUID.randomUUID().toString();
        DefaultResponse<Petugas> petugas = petugasService.getPetugasById(pusatEvakuasiRequest.getPetugasPencatat());
        PusatEvakuasi pusatEvakuasi = new PusatEvakuasi.Builder(pusatEvakuasiRequest.getLongitude(),
                pusatEvakuasiRequest.getLatitude()).alamatPusatEvakuasi(pusatEvakuasiRequest.getAlamatPusatEvakuasi())
                .catatan(pusatEvakuasiRequest.getCatatan())
                .idPusatEvakuasi(idPasar).petugasPencatat(petugas.getContent())
                .filePath(pusatEvakuasiRequest.getFilePath()).build();
        PusatEvakuasi pusatEvakuasiSave = pusatEvakuasiRepository.save(pusatEvakuasi);
        return pusatEvakuasiSave;
    }

    public PusatEvakuasi getPusatEvakuasiById(String id) throws IOException {
        PusatEvakuasi pusatEvakuasi = pusatEvakuasiRepository.findPusatEvakuasiById(id);
        return pusatEvakuasi;
    }

    public PusatEvakuasi updatePusatEvakuasi(String id, PusatEvakuasiRequest pusatEvakuasiRequest)
            throws IOException {
        PusatEvakuasi pusatEvakuasi = pusatEvakuasiRepository.findPusatEvakuasiById(id);
        Petugas petugasReview = petugasService.getPetugasById(pusatEvakuasiRequest.getPetugasPencatat()).getContent();
        if (pusatEvakuasi != null) {
            pusatEvakuasi.setAlamatPusatEvakuasi(pusatEvakuasiRequest.getAlamatPusatEvakuasi());
            pusatEvakuasi.setCatatan(pusatEvakuasiRequest.getCatatan());
            pusatEvakuasi.setFilePath(pusatEvakuasiRequest.getFilePath());
            pusatEvakuasi.setPetugasPencatat(petugasReview);
        }
        PusatEvakuasi pusatEvakuasiUpdated = pusatEvakuasiRepository.save(pusatEvakuasi);
        return pusatEvakuasiUpdated;
    }
}
