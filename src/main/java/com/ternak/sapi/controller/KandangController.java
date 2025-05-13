package com.ternak.sapi.controller;

import com.ternak.sapi.config.PathConfig;
import com.ternak.sapi.model.Kandang;
import com.ternak.sapi.model.Kandang;
import com.ternak.sapi.payload.*;
import com.ternak.sapi.service.KandangService;
import com.ternak.sapi.util.AppConstants;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
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
@RequestMapping("/api/kandang")
public class KandangController {
    private static final Logger logger = LoggerFactory.getLogger(KandangController.class);

    private KandangService kandangService = new KandangService();

    @Autowired
    private FileSystem fileSystem;

    @GetMapping
    public PagedResponse<Kandang> getKandangs(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "peternakID", defaultValue = "*") String peternakID) throws IOException {
        return kandangService.getAllKandang(page, size, peternakID);
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity<byte[]> getFileFromHDFS(@PathVariable String fileName) {
        String uri = "hdfs://hadoop-master:9000/kandang/" + fileName;
        // String uri = "hdfs://h-primary:6912/kandang/" + fileName;
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
    public ResponseEntity<?> createKandang(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @ModelAttribute KandangRequest kandangRequest) throws IOException {

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
                String hdfsDir = "hdfs://hadoop-master:9000/kandang/" + newFileName + fileExtension;
                Configuration configuration = new Configuration();
                FileSystem fs = FileSystem.get(URI.create(uri), configuration);
                fs.copyFromLocalFile(new Path(localPath), new Path(hdfsDir));
                String savePath = "file/" + newFileName + fileExtension;

                newFile.delete();
                Kandang kandang = kandangService.createKandang(kandangRequest, savePath);

                if (kandang == null) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Please check relational ID"));
                } else {
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{idKandang}")
                            .buildAndExpand(kandang.getIdKandang()).toUri();

                    return ResponseEntity.created(location)
                            .body(new ApiResponse(true, "Kandang Created Successfully"));
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
                Kandang kandang = kandangService.createKandang(kandangRequest, "");

                if (kandang == null) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Please check relational ID"));
                } else {
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{idKandang}")
                            .buildAndExpand(kandang.getIdKandang()).toUri();

                    return ResponseEntity.created(location)
                            .body(new ApiResponse(true, "Kandang Created Successfully"));
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

    @GetMapping("/{kandangId}")
    public DefaultResponse<Kandang> getKandangById(@PathVariable String kandangId) throws IOException {
        return kandangService.getKandangById(kandangId);
    }

    @PutMapping("/{kandangId}")
    public ResponseEntity<?> updateKandang(@PathVariable String kandangId,
            @RequestPart(value = "file", required = false) MultipartFile file, @ModelAttribute KandangRequest kandangRequest)
            throws IOException {
        System.out.println("File Dicontroller  " + file);
        if(file != null && !file.isEmpty()) {
            System.out.println("File tidak null");
            // upload file
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
                String hdfsDir = "hdfs://hadoop-master:9000/kandang/" + newFileName + fileExtension;
                Configuration configuration = new Configuration();
                FileSystem fs = FileSystem.get(URI.create(uri), configuration);
                fs.copyFromLocalFile(new Path(localPath), new Path(hdfsDir));
                String savePath = "file/" + newFileName + fileExtension;

                newFile.delete();
                Kandang kandang = kandangService.updateKandang(kandangId, kandangRequest, savePath);

                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest().path("/{kandangId}")
                        .buildAndExpand(kandang.getIdKandang()).toUri();

                return ResponseEntity.created(location)
                        .body(new ApiResponse(true, "Kandang Updated Successfully"));
            } catch (IOException e) {
                // Penanganan kesalahan saat menyimpan file
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Cannot Upload File into Hadoop"));
            }
        }else{
            System.out.println("file null");
            try {
                Kandang kandang = kandangService.updateKandang(kandangId, kandangRequest, "");
                if (kandang == null) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Please check relational ID"));
                } else {
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{idKandang}")
                            .buildAndExpand(kandang.getIdKandang()).toUri();

                    return ResponseEntity.created(location)
                            .body(new ApiResponse(true, "Kandang Created Successfully"));
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

    @DeleteMapping("/{kandangId}")
    public HttpStatus deleteKandang(@PathVariable(value = "kandangId") String kandangId) throws IOException {
        kandangService.deleteKandangById(kandangId);
        return HttpStatus.FORBIDDEN;
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkKandang(@RequestBody List<KandangRequest> kandangRequests) {
        try {
            kandangService.createBulkKandang(kandangRequests); // Passing List<KandangRequest>
            return ResponseEntity.ok(new ApiResponse(true, "All Kandang Created Successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to create bulk data: " + e.getMessage()));
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> createImportKandang(@RequestBody List<KandangRequest> kandangRequests) {
        try {
            kandangService.createImportKandang(kandangRequests); // Passing List<KandangRequest>
            return ResponseEntity.ok(new ApiResponse(true, "All Kandang Created Successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to create bulk data: " + e.getMessage()));
        }
    }

    @PostMapping("/bulkNama")
    public ResponseEntity<?> createImportKandangByNama(@RequestBody List<KandangRequest> kandangRequests) {
        try {
            kandangService.createImportKandangByNama(kandangRequests); // Passing List<KandangRequest>
            return ResponseEntity.ok(new ApiResponse(true, "All Kandang Created Successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to create bulk data: " + e.getMessage()));
        }
    }

}
