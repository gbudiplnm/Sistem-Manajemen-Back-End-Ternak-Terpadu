package com.ternak.sapi.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.ternak.sapi.model.Puskesmas;
import com.ternak.sapi.exception.ApiException;
import com.ternak.sapi.exception.BadRequestException;
import com.ternak.sapi.model.Petugas;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.PagedResponse;
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
        String [] path = new String[puskesmasRequest.getFilePath().length];
        int i = 0;
        for (MultipartFile file : puskesmasRequest.getFilePath()) {
            String idGambar = java.util.UUID.randomUUID().toString();
            String filePath = hadoopFileService.uploadFile(idGambar, file, "puskesmas");
            path[i] = filePath;
            i++;
        }
        Puskesmas puskesmas = new Puskesmas.Builder(puskesmasRequest.getNamaPuskesmas(),
                puskesmasRequest.getLongitude(), puskesmasRequest.getLatitude())
                .alamat(puskesmasRequest.getAlamat())
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

    public Puskesmas updatePuskesmas(String id, PuskesmasRequest puskesmasRequest) throws IOException {
        Puskesmas puskesmas = puskesmasRepository.findPuskesmasById(id);
        Petugas petugasReview = petugasService.getPetugasById(puskesmasRequest.getPetugasPencatat()).getContent();
        String [] path = new String[puskesmasRequest.getFilePath().length];
        int i = 0;
        for (MultipartFile file : puskesmasRequest.getFilePath()) {
            String filePath = hadoopFileService.uploadFile(puskesmas.getIdPuskesmas(), file, "puskesmas");
            path[i] = filePath;
            i++;
        }
        if (puskesmas != null) {
            puskesmas.setAlamat(puskesmasRequest.getAlamat());
            puskesmas.setCatatan(puskesmasRequest.getCatatan());
            puskesmas.setFilePath(path);
            puskesmas.setPetugasPencatat(petugasReview);
        }
        Puskesmas puskesmasUpdated = puskesmasRepository.save(puskesmas);
        return puskesmasUpdated;
    }
}
