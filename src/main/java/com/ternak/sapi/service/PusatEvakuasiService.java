package com.ternak.sapi.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.ternak.sapi.model.PusatEvakuasi;
import com.ternak.sapi.exception.ApiException;
import com.ternak.sapi.model.Petugas;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PusatEvakuasiEditRequest;
import com.ternak.sapi.payload.PusatEvakuasiRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.repository.PusatEvakuasiRepository;
import com.ternak.sapi.util.AppUtility;

public class PusatEvakuasiService {

    private PusatEvakuasiRepository pusatEvakuasiRepository = new PusatEvakuasiRepository();
    private PetugasService petugasService = new PetugasService();
    private HadoopFileService hadoopFileService = new HadoopFileService();

    public PagedResponse<PusatEvakuasi> getAllPusatEvakuasi(UniversalQueries query) throws IOException {
        AppUtility.validatePageNumberAndSize(query.getPage(), query.getSize());
        List<PusatEvakuasi> pusatEvakuasiList = pusatEvakuasiRepository.findAll(query.getSize());
        return new PagedResponse<>(pusatEvakuasiList, pusatEvakuasiList.size(), "Successfully get data", 200);
    }

    public PusatEvakuasi savePusatEvakuasi(PusatEvakuasiRequest pusatEvakuasiRequest) throws IOException {
        String idPasar = java.util.UUID.randomUUID().toString();
        DefaultResponse<Petugas> petugas = petugasService.getPetugasById(pusatEvakuasiRequest.getPetugasPencatat());
        List<String> pathLists = new java.util.ArrayList<>();
        for (MultipartFile file : pusatEvakuasiRequest.getFilePath()) {
            String idGambar = java.util.UUID.randomUUID().toString();
            String filePath = hadoopFileService.uploadFile(idGambar, file, "pusat-evakuasi");
            pathLists.add(filePath);
        }
        String[] path = Arrays.copyOf(pathLists.toArray(), pathLists.size(), String[].class);
        PusatEvakuasi pusatEvakuasi = new PusatEvakuasi.Builder(pusatEvakuasiRequest.getLongitude(),
                pusatEvakuasiRequest.getLatitude()).desa(pusatEvakuasiRequest.getDesa())
                .kecamatan(pusatEvakuasiRequest.getKecamatan())
                .kabupatenKota(pusatEvakuasiRequest.getKabupatenKota()).provinsi(pusatEvakuasiRequest.getProvinsi())
                .namaPusatEvakuasi(pusatEvakuasiRequest.getNamaPusatEvakuasi())
                .catatan(pusatEvakuasiRequest.getCatatan())
                .tempatEvakuasiBencana(pusatEvakuasiRequest.getTempatEvakuasiBencana())
                .jenisBencana(pusatEvakuasiRequest.getJenisBencana())
                .petaEvakuasi(pusatEvakuasiRequest.getPetaEvakuasi())
                .jalurEvakuasi(pusatEvakuasiRequest.getJalurEvakuasi())
                .idPusatEvakuasi(idPasar).petugasPencatat(petugas.getContent())
                .filePath(path).build();
        PusatEvakuasi pusatEvakuasiSave = pusatEvakuasiRepository.save(pusatEvakuasi);
        return pusatEvakuasiSave;
    }

    public PusatEvakuasi getPusatEvakuasiById(String id) throws IOException {
        PusatEvakuasi pusatEvakuasi = pusatEvakuasiRepository.findPusatEvakuasiById(id);
        return pusatEvakuasi;
    }

    public PusatEvakuasi updatePusatEvakuasi(String id, PusatEvakuasiEditRequest pusatEvakuasiRequest)
            throws IOException {
        PusatEvakuasi pusatEvakuasi = pusatEvakuasiRepository.findPusatEvakuasiById(id);
        if (pusatEvakuasi != null) {
            if (pusatEvakuasiRequest.getLatitude() != null) {
                pusatEvakuasi.setLatitude(pusatEvakuasiRequest.getLatitude());
            }
            if (pusatEvakuasiRequest.getLongitude() != null) {
                pusatEvakuasi.setLongitude(pusatEvakuasiRequest.getLongitude());
            }
            if (pusatEvakuasiRequest.getDesa() != null) {
                pusatEvakuasi.setDesa(pusatEvakuasiRequest.getDesa());
            }
            if (pusatEvakuasiRequest.getKecamatan() != null) {
                pusatEvakuasi.setKecamatan(pusatEvakuasiRequest.getKecamatan());
            }
            if (pusatEvakuasiRequest.getKabupatenKota() != null) {
                pusatEvakuasi.setKabupatenKota(pusatEvakuasiRequest.getKabupatenKota());
            }
            if (pusatEvakuasiRequest.getProvinsi() != null) {
                pusatEvakuasi.setProvinsi(pusatEvakuasiRequest.getProvinsi());
            }
            if (pusatEvakuasiRequest.getNamaPusatEvakuasi() != null) {
                pusatEvakuasi.setNamaPusatEvakuasi(pusatEvakuasiRequest.getNamaPusatEvakuasi());
            }
            if (pusatEvakuasiRequest.getCatatan() != null) {
                pusatEvakuasi.setCatatan(pusatEvakuasiRequest.getCatatan());
            }
            if (pusatEvakuasiRequest.getTempatEvakuasiBencana() != null) {
                pusatEvakuasi.setTempatEvakuasiBencana(pusatEvakuasiRequest.getTempatEvakuasiBencana());
            }
            if (pusatEvakuasiRequest.getJenisBencana() != null) {
                pusatEvakuasi.setJenisBencana(pusatEvakuasiRequest.getJenisBencana());
            }
            if (pusatEvakuasiRequest.getPetaEvakuasi() != null) {
                pusatEvakuasi.setPetaEvakuasi(pusatEvakuasiRequest.getPetaEvakuasi());
            }
            if (pusatEvakuasiRequest.getJalurEvakuasi() != null) {
                pusatEvakuasi.setJalurEvakuasi(pusatEvakuasiRequest.getJalurEvakuasi());
            }
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND, "Data Pusat Evakuasi Tidak Ditemukan");
        }
        PusatEvakuasi pusatEvakuasiUpdated = pusatEvakuasiRepository.save(pusatEvakuasi);
        return pusatEvakuasiUpdated;
    }

    public boolean deleteFile(String path, String id) throws IOException {
        try {
            PusatEvakuasi pusatEvakuasi = pusatEvakuasiRepository.findPusatEvakuasiById(id);
            String[] oldPaths = pusatEvakuasi.getFilePath();
            String[] newPaths = new String[oldPaths.length - 1];
            for (String string : oldPaths) {
                if (!string.equalsIgnoreCase(path)) {
                    newPaths[newPaths.length - newPaths.length] = string;
                }
            }
            pusatEvakuasi.setFilePath(newPaths);

            pusatEvakuasiRepository.save(pusatEvakuasi);
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
            String filePath = hadoopFileService.uploadFile(idGambar, files[i], "pusat-evakuasi");
            paths[i] = filePath;
        }
        try {
            PusatEvakuasi pusatEvakuasi = pusatEvakuasiRepository.findPusatEvakuasiById(id);
            String[] oldPaths = pusatEvakuasi.getFilePath();
            String[] newPaths = new String[oldPaths.length + paths.length];
            System.arraycopy(oldPaths, 0, newPaths, 0, oldPaths.length);
            System.arraycopy(paths, 0, newPaths, oldPaths.length, paths.length);
            pusatEvakuasi.setFilePath(newPaths);
            pusatEvakuasiRepository.save(pusatEvakuasi);
        } catch (Exception e) {
            for (String filePath : paths) {
                hadoopFileService.deleteFile(filePath);
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Terjadi Kesalahan");
        }
        return paths;
    }

}
