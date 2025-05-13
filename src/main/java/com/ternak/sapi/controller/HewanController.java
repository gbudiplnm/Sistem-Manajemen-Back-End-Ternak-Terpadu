package com.ternak.sapi.controller;

import com.ternak.sapi.config.PathConfig;
import com.ternak.sapi.model.Hewan;
import com.ternak.sapi.payload.*;
import com.ternak.sapi.service.HewanService;
import com.ternak.sapi.util.AppConstants;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/hewan")
public class HewanController {
    private HewanService hewanService = new HewanService();

    @GetMapping
    public PagedResponse<Hewan> getHewans(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "peternakID", defaultValue = "*") String peternakID,
            @RequestParam(value = "petugasID", defaultValue = "*") String petugasID,
            @RequestParam(value = "hewanID", defaultValue = "*") String hewanID,
            @RequestParam(value = "userID", defaultValue = "*") String userID) throws IOException {
        return hewanService.getAllHewan(page, size, peternakID, petugasID, hewanID, userID);
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity<byte[]> getFileFromHDFS(@PathVariable String fileName) {
        String uri = "hdfs://hadoop-master:9000/hewan/" + fileName;
        // String uri = "hdfs://h-primary:6912/hewan/" + fileName;
        Configuration configuration = new Configuration();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            FileSystem fs = FileSystem.get(URI.create(uri), configuration);
            Path filePath = new Path(uri);

            if (!fs.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            InputStream inputStream = fs.open(filePath);
            IOUtils.copyBytes(inputStream, outputStream, 4096, false);
            inputStream.close();
            fs.close();

            byte[] fileBytes = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(fileBytes.length);

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> createHewan(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @ModelAttribute HewanRequest hewanRequest) throws IOException {

        System.out.println(file);

        if (file != null && !file.isEmpty()) { // Perbaikan di sini
            // Proses upload file
            try {
                // Mendapatkan nama file asli
                String originalFileName = file.getOriginalFilename();

                // Memeriksa apakah originalFileName tidak null
                if (originalFileName == null) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "File name is invalid"));
                }

                // Mendapatkan ekstensi file
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

                // Mendapatkan timestamp saat ini
                String timestamp = String.valueOf(System.currentTimeMillis());

                // Membuat UUID baru
                String uuid = UUID.randomUUID().toString();

                // Menggabungkan timestamp dan UUID
                String newFileName = "file_" + timestamp + "_" + uuid;
                String filePath = PathConfig.storagePath + "/" + newFileName + fileExtension;
                File newFile = new File(filePath);

                // Menyimpan file ke lokasi yang ditentukan di server
                file.transferTo(newFile);

                // Mendapatkan local path dari file yang disimpan
                String localPath = newFile.getAbsolutePath();
                String uri = "hdfs://hadoop-master:9000";
                String hdfsDir = "hdfs://hadoop-master:9000/hewan/" + newFileName + fileExtension;
                Configuration configuration = new Configuration();
                FileSystem fs = FileSystem.get(URI.create(uri), configuration);
                fs.copyFromLocalFile(new Path(localPath), new Path(hdfsDir));
                String savePath = "file/" + newFileName + fileExtension;

                newFile.delete();
                Hewan hewan = hewanService.createHewan(hewanRequest, savePath);

                if (hewan == null) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Please check relational ID"));
                } else {
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{idHewan}")
                            .buildAndExpand(hewan.getIdHewan()).toUri();

                    return ResponseEntity.created(location)
                            .body(new ApiResponse(true, "Hewan Created Successfully"));
                }
            } catch (IOException e) {
                // Penanganan kesalahan saat menyimpan file
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Cannot Upload File into Hadoop"));
            }
        } else {
            // Tidak ada input file
            try {
                Hewan hewan = hewanService.createHewan(hewanRequest, "");

                if (hewan == null) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Please check relational ID"));
                } else {
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{idHewan}")
                            .buildAndExpand(hewan.getIdHewan()).toUri();

                    return ResponseEntity.created(location)
                            .body(new ApiResponse(true, "Hewan Created Successfully"));
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, e.getMessage()));
            } catch (IOException e) {
                // Penanganan kesalahan saat menyimpan file
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Cannot Upload File into Hadoop"));
            }
        }
    }

    @GetMapping("/{idHewan}")
    public DefaultResponse<Hewan> getHewanById(@PathVariable String idHewan) throws IOException {
        return hewanService.getHewanById(idHewan);
    }

    @PutMapping("/{idHewan}")
    public ResponseEntity<?> updateHewan(@PathVariable String idHewan,
            @RequestPart(value = "file",required = false) MultipartFile file, @ModelAttribute HewanRequest hewanRequest) throws IOException {
        // upload file
        if(file != null && !file.isEmpty()) {
            try {
                // Mendapatkan nama file asli
                String originalFileName = file.getOriginalFilename();

                // Mendapatkan ekstensi file
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

                // Mendapatkan timestamp saat ini
                String timestamp = String.valueOf(System.currentTimeMillis());

                // Membuat UUID baru
                String uuid = UUID.randomUUID().toString();

                // Menggabungkan timestamp dan UUID
                String newFileName = "file_" + timestamp + "_" + uuid;
                String filePath = PathConfig.storagePath + "/" + newFileName + fileExtension;
                File newFile = new File(filePath);

                // Menyimpan file ke lokasi yang ditentukan di server
                file.transferTo(newFile);

                // Mendapatkan local path dari file yang disimpan
                String localPath = newFile.getAbsolutePath();
                String uri = "hdfs://hadoop-master:9000";
                String hdfsDir = "hdfs://hadoop-master:9000/hewan/" + newFileName + fileExtension;
                // String uri = "hdfs://h-primary:6912";
                // String hdfsDir = "hdfs://h-primary:6912/hewan/" + newFileName +
                // fileExtension;
                Configuration configuration = new Configuration();
                FileSystem fs = FileSystem.get(URI.create(uri), configuration);
                fs.copyFromLocalFile(new Path(localPath), new Path(hdfsDir));
                String savePath = "file/" + newFileName + fileExtension;

                newFile.delete();
                Hewan hewan = hewanService.updateHewan(idHewan, hewanRequest, savePath);

                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest().path("/{idHewan}")
                        .buildAndExpand(hewan.getIdHewan()).toUri();

                return ResponseEntity.created(location)
                        .body(new ApiResponse(true, "Hewan Updated Successfully"));
            } catch (IOException e) {
                // Penanganan kesalahan saat menyimpan file
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Cannot Upload File into Hadoop"));
            }
        }else{
            try {
                Hewan hewan = hewanService.updateHewan(idHewan,hewanRequest, "");

                if (hewan == null) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Please check relational ID"));
                } else {
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{idHewan}")
                            .buildAndExpand(hewan.getIdHewan()).toUri();

                    return ResponseEntity.created(location)
                            .body(new ApiResponse(true, "Hewan Created Successfully"));
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, e.getMessage()));
            } catch (IOException e) {
                // Penanganan kesalahan saat menyimpan file
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Cannot Upload File into Hadoop"));
            }
        }

    }

    @DeleteMapping("/{idHewan}")
    public HttpStatus deleteHewan(@PathVariable(value = "idHewan") String idHewan) throws IOException {
        System.out.println("id Hewan yang dikirim" + idHewan);
        hewanService.deleteHewanById(idHewan);
        return HttpStatus.FORBIDDEN;
    }

    @PostMapping("/import")
    public ResponseEntity<?> createHewanImport(@RequestBody List<HewanRequest> hewanRequests) throws IOException {
        try {
            System.out.println("Jumlah data yang diterima: " + hewanRequests.size());
            hewanService.createHewanImport(hewanRequests);
            return ResponseEntity.ok(new ApiResponse(true, "Hewan Created Successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to create bulk data: " + e.getMessage()));
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkHewan(@RequestBody List<HewanRequest> hewanRequests) throws IOException {
        try {
            System.out.println("Jumlah data yang diterima: " + hewanRequests.size());
            hewanService.createBulkHewan(hewanRequests);
            return ResponseEntity.ok(new ApiResponse(true, "Hewan Created Successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to create bulk data: " + e.getMessage()));
        }
    }

    @PostMapping("/bulkNama")
    public ResponseEntity<?> createBulkHewanImport(@RequestBody List<HewanRequest> hewanRequests) throws IOException {
        try {
            System.out.println("Jumlah data yang diterima: " + hewanRequests.size());
            hewanService.createBulkHewanImport(hewanRequests);
            return ResponseEntity.ok(new ApiResponse(true, "Hewan Created Successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to create bulk data: " + e.getMessage()));
        }
    }

}
