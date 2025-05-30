package com.ternak.sapi.service;

// import com.ternak.sapi.repository.UserRepository;
import com.ternak.sapi.model.Hewan;
import com.ternak.sapi.model.Inseminasi;
import com.ternak.sapi.model.Kelahiran;
import com.ternak.sapi.model.Pengobatan;
import com.ternak.sapi.model.Peternak;
import com.ternak.sapi.repository.HewanRepository;
import com.ternak.sapi.repository.InseminasiRepository;
import com.ternak.sapi.repository.KelahiranRepository;
import com.ternak.sapi.repository.PengobatanRepository;
import com.ternak.sapi.repository.PeternakRepository;
import com.ternak.sapi.repository.PetugasRepository;
import com.ternak.sapi.repository.PkbRepository;
import com.ternak.sapi.repository.VaksinRepository;
import com.ternak.sapi.model.Petugas;
import com.ternak.sapi.model.Pkb;
import com.ternak.sapi.model.Vaksin;
import com.ternak.sapi.exception.BadRequestException;
import com.ternak.sapi.exception.ResourceNotFoundException;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.PetugasRequest;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.util.AppConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.stereotype.Service;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PetugasService {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "petugasdev";
    private PetugasRepository petugasRepository = new PetugasRepository();
    private PeternakRepository peternakRepository = new PeternakRepository();
    private HewanRepository hewanRepository = new HewanRepository();
    private VaksinRepository vaksinRepository = new VaksinRepository();
    private InseminasiRepository inseminasiRepository = new InseminasiRepository();
    private KelahiranRepository kelahiranRepository = new KelahiranRepository();
    private PengobatanRepository pengobatanRepository = new PengobatanRepository();
    private PkbRepository pkbRepository = new PkbRepository();
    // private UserRepository userRepository = new UserRepository();

    // private static final Logger logger =
    // LoggerFactory.getLogger(PetugasService.class);

    public PagedResponse<Petugas> getAllPetugas(int page, int size, String userID) throws IOException {
        validatePageNumberAndSize(page, size);
        List<Petugas> petugasResponse = new ArrayList<>();

        if (userID.equalsIgnoreCase("*"))
            petugasResponse = petugasRepository.findAll(size);
        if (!userID.equalsIgnoreCase("*"))
            petugasResponse = petugasRepository.findAllByUserID(userID, size);

        return new PagedResponse<>(petugasResponse, petugasResponse.size(), "Successfully get data", 200);
    }

    public Petugas createPetugas(PetugasRequest petugasRequest) throws IOException {

        Petugas dataPetugas = null;

        // Memeriksa apakah NIK, Email, atau NoTelp tidak sama dengan "-"
        if (!petugasRequest.getNikPetugas().equals("-") &&
                !petugasRequest.getEmail().equals("-") &&
                !petugasRequest.getNoTelp().equals("-")) {

            // Validasi jika NIK Petugas sudah terdaftar
            if (petugasRepository.existsByNikPetugas(petugasRequest.getNikPetugas())) {
                throw new IllegalArgumentException("NIK Petugas sudah terdaftar!");
            }

            // Validasi jika Email Petugas sudah ada
            if (petugasRepository.existsByEmail(petugasRequest.getEmail())) {
                throw new IllegalArgumentException("Email sudah digunakan!");
            }

            // Validasi jika Nomor Telepon sudah ada
            if (petugasRepository.existsByNoTelp(petugasRequest.getNoTelp())) {
                throw new IllegalArgumentException("Nomor Telepon sudah terdaftar!");
            }
        }

        // Membuat objek Petugas dan mengisi datanya
        Petugas petugas = new Petugas();
        petugas.setPetugasId(
                petugasRequest.getPetugasId() != null ? petugasRequest.getPetugasId() : UUID.randomUUID().toString());
        petugas.setNikPetugas(petugasRequest.getNikPetugas());
        petugas.setNamaPetugas(petugasRequest.getNamaPetugas());
        petugas.setNoTelp(petugasRequest.getNoTelp());
        petugas.setEmail(petugasRequest.getEmail());
        petugas.setJob(petugasRequest.getJob());
        petugas.setWilayah(petugasRequest.getWilayah());

        // Menyimpan data Petugas ke database
        dataPetugas = petugasRepository.save(petugas);

        return dataPetugas;
    }

    public DefaultResponse<Petugas> getPetugasById(String petugasId) throws IOException {
        // Retrieve Petugas
        Petugas petugasResponse = petugasRepository.findById(petugasId);
        return new DefaultResponse<>(petugasResponse.isValid() ? petugasResponse : null,
                petugasResponse.isValid() ? 1 : 0, "Successfully get data");
    }

    public DefaultResponse<Petugas> getPetugasByNik(String nik) throws IOException {
        // Retrieve Petugas
        Petugas petugasResponse = petugasRepository.findByNik(nik);
        return new DefaultResponse<>(petugasResponse.isValid() ? petugasResponse : null,
                petugasResponse.isValid() ? 1 : 0, "Successfully get data");
    }

    // public void save(Hewan hewan) throws IOException {
    // // Simulasi penyimpanan hewan, sesuaikan dengan HBaseClient Anda
    // hBaseClient.saveData("hewanTable", hewan);
    // }

    public Petugas updatePetugas(String petugasId, PetugasRequest petugasRequest) throws IOException {

        Petugas petugas = new Petugas();
        petugas.setNikPetugas(petugasRequest.getNikPetugas());
        petugas.setNamaPetugas(petugasRequest.getNamaPetugas());
        petugas.setNoTelp(petugasRequest.getNoTelp());
        petugas.setEmail(petugasRequest.getEmail());
        petugas.setJob(petugasRequest.getJob());
        petugas.setWilayah(petugasRequest.getWilayah());

        List<Hewan> hewanList = hewanRepository.findByPetugasId(petugasId);
        if (hewanList != null) {
            for (Hewan hewan : hewanList) {
                // Update field hewan sesuai dengan kebutuhan
                hewan.setPetugas(petugas);
                hewanRepository.updatePetugasByHewan(hewan.getIdHewan(), hewan);
            }
        }

        List<Peternak> peternakList = peternakRepository.findByPetugasId(petugasId);
        if (peternakList != null) {
            // Jika peternak ditemukan, lakukan update pada petugas yang terkait
            for (Peternak peternak : peternakList) {
                // Update field hewan sesuai dengan kebutuhan
                peternak.setPetugas(petugas);
                peternakRepository.updatePetugasByPeternak(peternak.getIdPeternak(), peternak);
            }
        }

        List<Vaksin> vaksinList = vaksinRepository.findByPetugasId(petugasId);
        if (vaksinList != null) {
            // Jika vaksin ditemukan, lakukan update pada petugas yang terkait
            for (Vaksin vaksin : vaksinList) {
                // Update field hewan sesuai dengan kebutuhan
                vaksin.setPetugas(petugas);
                vaksinRepository.updatePetugasByVaksin(vaksin.getIdVaksin(), vaksin);
            }
        }

        List<Inseminasi> inseminasiList = inseminasiRepository.findByPetugasId(petugasId);
        if (inseminasiList != null) {
            // Jika inseminasi ditemukan, lakukan update pada petugas yang terkait
            for (Inseminasi inseminasi : inseminasiList) {
                // Update field hewan sesuai dengan kebutuhan
                inseminasi.setPetugas(petugas);
                inseminasiRepository.updatePetugasByInseminasi(inseminasi.getIdInseminasi(), inseminasi);
            }
        }

        List<Kelahiran> kelahiranList = kelahiranRepository.findByPetugasId(petugasId);
        if (kelahiranList != null) {
            // Jika kelahiran ditemukan, lakukan update pada petugas yang terkait
            for (Kelahiran kelahiran : kelahiranList) {
                // Update field hewan sesuai dengan kebutuhan
                kelahiran.setPetugas(petugas);
                kelahiranRepository.updatePetugasByKelahiran(kelahiran.getIdKelahiran(), kelahiran);
            }
        }

        List<Pengobatan> pengobatanList = pengobatanRepository.findByPetugasId(petugasId);
        if (pengobatanList != null) {
            // Jika pengobatan ditemukan, lakukan update pada petugas yang terkait
            for (Pengobatan pengobatan : pengobatanList) {
                // Update field hewan sesuai dengan kebutuhan
                pengobatan.setPetugas(petugas);
                pengobatanRepository.updatePetugasByPengobatan(pengobatan.getIdPengobatan(), pengobatan);
            }
        }

        List<Pkb> pkbList = pkbRepository.findByPetugasId(petugasId);
        if (pkbList != null) {
            // Jika pkb ditemukan, lakukan update pada petugas yang terkait
            for (Pkb pkb : pkbList) {
                // Update field hewan sesuai dengan kebutuhan
                pkb.setPetugas(petugas);
                pkbRepository.updatePetugasByPkb(pkb.getIdPkb(), pkb);
            }
        }

        return petugasRepository.update(petugasId, petugas);
    }

    public void deletePetugasById(String idPetugas) throws IOException {
        petugasRepository.deleteById(idPetugas);
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
    public void createBulkPetugas(List<PetugasRequest> petugasRequests) throws IOException {
        System.out.println("Memulai proses penyimpanan data petugas secara bulk...");

        // Extract lists of unique identifiers (NIK, Email, NoTelp) from the incoming
        // requests
        List<String> nikList = petugasRequests.stream().map(PetugasRequest::getNikPetugas).collect(Collectors.toList());
        List<String> emailList = petugasRequests.stream().map(PetugasRequest::getEmail).collect(Collectors.toList());
        List<String> noTelpList = petugasRequests.stream().map(PetugasRequest::getNoTelp).collect(Collectors.toList());

        // Check which NIK, Email, and NoTelp already exist
        System.out.println("Memeriksa NIK, Email, dan NoTelp yang sudah terdaftar...");
        Set<String> existingNikSet = new HashSet<>(petugasRepository.findExistingNik(nikList));
        Set<String> existingEmailSet = new HashSet<>(petugasRepository.findExistingEmail(emailList));
        Set<String> existingNoTelpSet = new HashSet<>(petugasRepository.findExistingNoTelp(noTelpList));

        List<Petugas> petugasList = new ArrayList<>();
        int skippedIncomplete = 0;
        int skippedExisting = 0;

        // Process each PetugasRequest
        for (PetugasRequest request : petugasRequests) {
            // Skip if any required field is null
            if (request.getNikPetugas() == null || request.getNamaPetugas() == null ||
                    request.getNoTelp() == null || request.getEmail() == null) {
                System.out.println("Data tidak lengkap, melewati data: " + request);
                skippedIncomplete++;
                continue;
            }

            // Skip if NIK, Email, or NoTelp already exist
            if (existingNikSet.contains(request.getNikPetugas())) {
                System.out.println("NIK sudah terdaftar, melewati NIK: " + request.getNikPetugas());
                skippedExisting++;
                continue;
            }
            if (existingEmailSet.contains(request.getEmail())) {
                System.out.println("Email sudah digunakan, melewati Email: " + request.getEmail());
                skippedExisting++;
                continue;
            }
            if (existingNoTelpSet.contains(request.getNoTelp())) {
                System.out.println("Nomor Telepon sudah terdaftar, melewati NoTelp: " + request.getNoTelp());
                skippedExisting++;
                continue;
            }

            // Create the Petugas object and add to the list
            Petugas petugas = new Petugas();
            petugas.setPetugasId(
                    request.getPetugasId() != null ? request.getPetugasId() : UUID.randomUUID().toString());
            petugas.setNikPetugas(request.getNikPetugas());
            petugas.setNamaPetugas(request.getNamaPetugas());
            petugas.setNoTelp(request.getNoTelp());
            petugas.setEmail(request.getEmail());
            petugas.setJob(request.getJob() != null ? request.getJob() : "-"); // Default job kosong jika null
            petugas.setWilayah(request.getWilayah() != null ? request.getWilayah() : "-"); // Default wilayah kosong
                                                                                           // jika
                                                                                           // null
            petugasList.add(petugas);

            System.out.println("Menambahkan data petugas ke dalam daftar: " + petugas.getNikPetugas());
        }

        // Save only valid Petugas data
        if (!petugasList.isEmpty()) {
            System.out.println("Menyimpan data petugas yang valid...");
            petugasRepository.saveAll(petugasList);
            System.out.println("Proses penyimpanan selesai. Total data yang disimpan: " + petugasList.size());
        } else {
            System.out.println("Tidak ada data petugas baru yang valid untuk disimpan.");
        }

        System.out.println("Proses selesai. Data tidak lengkap: " + skippedIncomplete + ", Data sudah terdaftar: "
                + skippedExisting);
    }

    @Transactional
    public void createImportPetugasByNama(List<PetugasRequest> petugasRequests) throws IOException {
        System.out.println("Memulai proses penyimpanan data petugas secara bulk...");
        // Check which NIK, Email, and NoTelp already exist
        List<Petugas> petugasList = new ArrayList<>();
        int skippedIncomplete = 0;
        int skippedExisting = 0;

        // Process each PetugasRequest
        for (PetugasRequest request : petugasRequests) {
            // Skip if any required field is null
            if (request.getNamaPetugas() == null) {
                System.out.println("Data tidak lengkap, melewati data: " + request);
                skippedIncomplete++;
                continue;
            }
            // Create the Petugas object and add to the list
            Petugas petugas = new Petugas();
            petugas.setPetugasId(
                    request.getPetugasId() != null ? request.getPetugasId() : UUID.randomUUID().toString());
            petugas.setNikPetugas(request.getNikPetugas() != null ? request.getNikPetugas() : "-");
            petugas.setNamaPetugas(request.getNamaPetugas() != null ? request.getNamaPetugas() : "-");
            petugas.setNoTelp(request.getNoTelp() != null ? request.getNoTelp() : "-");
            petugas.setEmail(request.getEmail() != null ? request.getEmail() : "-");
            petugas.setJob(request.getJob() != null ? request.getJob() : "-");
            petugas.setWilayah(request.getWilayah() != null ? request.getWilayah() : "-");
            petugasList.add(petugas);

            System.out.println("Menambahkan data petugas ke dalam daftar: " + petugas.getNamaPetugas());
        }

        // Save only valid Petugas data
        if (!petugasList.isEmpty()) {
            System.out.println("Menyimpan data petugas yang valid...");
            petugasRepository.saveByNama(petugasList);
            System.out.println("Proses penyimpanan selesai. Total data yang disimpan: " + petugasList.size());
        } else {
            System.out.println("Tidak ada data petugas baru yang valid untuk disimpan.");
        }

        System.out.println("Proses selesai. Data tidak lengkap: " + skippedIncomplete + ", Data sudah terdaftar: "
                + skippedExisting);
    }
}
