package com.ternak.sapi.service;

import com.ternak.sapi.exception.BadRequestException;
import com.ternak.sapi.model.Hewan;
import com.ternak.sapi.model.Inseminasi;
import com.ternak.sapi.model.JenisHewan;
import com.ternak.sapi.model.Kandang;
import com.ternak.sapi.model.Kelahiran;
import com.ternak.sapi.model.Pkb;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.JenisHewanRequest;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.repository.HewanRepository;
import com.ternak.sapi.repository.InseminasiRepository;
import com.ternak.sapi.repository.JenisHewanRepository;
import com.ternak.sapi.repository.KandangRepository;
import com.ternak.sapi.repository.KelahiranRepository;
import com.ternak.sapi.repository.PkbRepository;
import com.ternak.sapi.util.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JenisHewanService {
    private JenisHewanRepository jenishewanRepository = new JenisHewanRepository();
    private HewanRepository hewanRepository = new HewanRepository();
    private KandangRepository kandangRepository = new KandangRepository();
    private InseminasiRepository inseminasiRepository = new InseminasiRepository();
    private KelahiranRepository kelahiranRepository = new KelahiranRepository();
    private PkbRepository pkbRepository = new PkbRepository();

    public PagedResponse<JenisHewan> getAllJenisHewan(int page, int size, String peternakId, String hewanId,
            String kandangId) throws IOException {
        validatePageNumberAndSize(page, size);

        // Retrieve Polls
        List<JenisHewan> jenishewanResponse = new ArrayList<>();

        jenishewanResponse = jenishewanRepository.findAll(size);

        return new PagedResponse<>(jenishewanResponse, jenishewanResponse.size(), "Successfully get data", 200);
    }

    public JenisHewan createJenisHewan(JenisHewanRequest jenishewanRequest) throws IOException {
        // Validasi jika jenis hewan sudah ada
        if (jenishewanRepository.existsByJenis(jenishewanRequest.getJenis())) {
            throw new IllegalArgumentException("Jenis Hewan sudah terdaftar!");
        }

        String uuid = java.util.UUID.randomUUID().toString();

        JenisHewan jenishewan = new JenisHewan();
        if (jenishewanRequest.getIdJenisHewan() == null) {
            jenishewan.setIdJenisHewan(uuid);
        } else {
            jenishewan.setIdJenisHewan(jenishewanRequest.getIdJenisHewan());
        }
        jenishewan.setJenis(jenishewanRequest.getJenis());
        jenishewan.setDeskripsi(jenishewanRequest.getDeskripsi());
        return jenishewanRepository.save(jenishewan);
    }

    public DefaultResponse<JenisHewan> getJenisHewanById(String jenishewanId) throws IOException {
        // Retrieve Hewan
        JenisHewan jenishewanResponse = jenishewanRepository.findById(jenishewanId);
        return new DefaultResponse<>(jenishewanResponse.isValid() ? jenishewanResponse : null,
                jenishewanResponse.isValid() ? 1 : 0, "Successfully get data");
    }

    public JenisHewan updateJenisHewan(String jenishewanId, JenisHewanRequest jenishewanRequest)
            throws IOException {
        JenisHewan jenishewan = new JenisHewan();
        jenishewan.setJenis(jenishewanRequest.getJenis());
        jenishewan.setDeskripsi(jenishewanRequest.getDeskripsi());

        List<Hewan> hewanList = hewanRepository.findByJenisHewanId(jenishewanId);
        if (hewanList != null) {
            for (Hewan hewan : hewanList) {
                hewan.setJenisHewan(jenishewan);
                hewanRepository.updateJenisHewanByHewan(hewan.getIdHewan(), hewan);
            }
        }

        List<Kandang> kandangList = kandangRepository.findByJenisHewanId(jenishewanId);
        if (kandangList != null) {
            for (Kandang kandang : kandangList) {
                kandang.setJenisHewan(jenishewan);
                kandangRepository.updateJenisHewanByKandang(kandang.getIdKandang(), kandang);
            }
        }

        List<Inseminasi> inseminasiList = inseminasiRepository.findByJenisHewanId(jenishewanId);
        if (inseminasiList != null) {
            for (Inseminasi inseminasi : inseminasiList) {
                inseminasi.setJenisHewan(jenishewan);
                inseminasiRepository.updateJenisHewanByInseminasi(inseminasi.getIdInseminasi(), inseminasi);
            }
        }

        List<Kelahiran> kelahiranList = kelahiranRepository.findByJenisHewanId(jenishewanId);
        if (kelahiranList != null) {
            for (Kelahiran kelahiran : kelahiranList) {
                kelahiran.setJenisHewan(jenishewan);
                kelahiranRepository.updateJenisHewanByKelahiran(kelahiran.getIdKelahiran(), kelahiran);
            }
        }

        List<Pkb> pkbList = pkbRepository.findByJenisHewanId(jenishewanId);
        if (pkbList != null) {
            for (Pkb pkb : pkbList) {
                pkb.setJenisHewan(jenishewan);
                pkbRepository.updateJenisHewanByPkb(pkb.getIdPkb(), pkb);
            }
        }

        return jenishewanRepository.update(jenishewanId, jenishewan);
    }

    public void deleteJenisHewanById(String jenishewanId) throws IOException {
        jenishewanRepository.deleteById(jenishewanId);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    @Transactional
    public void createBulkJenisHewan(List<JenisHewanRequest> jenishewanRequests) throws IOException {
        System.out.println("Memulai proses penyimpanan data jenis hewan secara bulk...");

        List<JenisHewan> jenishewanList = new ArrayList<>();
        Set<String> jenisHewanSet = new HashSet<>();
        int skippedIncomplete = 0;

        for (JenisHewanRequest request : jenishewanRequests) {
            try {
                // Normalisasi string untuk konsistensi
                String jenisNormalized = request.getJenis() != null ? request.getJenis().trim().toLowerCase() : null;

                if (jenisNormalized == null || jenisNormalized.isEmpty()) {
                    System.out.println("Data jenis hewan tidak valid atau kosong: " + request);
                    skippedIncomplete++;
                    continue;
                }

                JenisHewan jenisHewanResponse = jenishewanRepository.findByJenis(jenisNormalized);
                if (jenisHewanResponse != null) {
                    System.out.println("Data '" + jenisNormalized + "' sudah ada di database");
                    continue;
                }

                if (jenisHewanSet.contains(jenisNormalized)) {
                    System.out.println("Jenis hewan '" + jenisNormalized + "' sudah ada dalam list");
                    continue;
                }

                JenisHewan jenishewan = new JenisHewan();
                jenishewan.setIdJenisHewan(request.getIdJenisHewan());
                jenishewan.setJenis(jenisNormalized);
                jenishewan.setDeskripsi(request.getDeskripsi() != null ? request.getDeskripsi() : "-");

                jenishewanList.add(jenishewan);
                jenisHewanSet.add(jenisNormalized); // Tambahkan jenis ke set
                System.out.println("Menambahkan data jenis hewan ke dalam daftar: " + jenisNormalized);
            } catch (Exception e) {
                System.err.println("Terjadi kesalahan saat memproses data: " + request);
                e.printStackTrace();
            }
        }

        // Simpan semua data valid ke dalam database
        if (!jenishewanList.isEmpty()) {
            System.out.println("Menyimpan data jenis hewan yang valid...");
            jenishewanRepository.saveAll(jenishewanList);
            System.out.println("Data jenis hewan berhasil disimpan. Total: " + jenishewanList.size());
        } else {
            System.out.println("Tidak ada data jenis hewan baru yang valid untuk disimpan.");
        }

        System.out.println("Proses selesai. Data tidak lengkap: " + skippedIncomplete);
    }

}