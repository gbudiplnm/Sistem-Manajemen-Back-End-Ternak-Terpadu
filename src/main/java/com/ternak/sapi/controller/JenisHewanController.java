package com.ternak.sapi.controller;

import com.ternak.sapi.config.PathConfig;
import com.ternak.sapi.model.JenisHewan;
import com.ternak.sapi.model.Peternak;
import com.ternak.sapi.model.TujuanPemeliharaan;
import com.ternak.sapi.payload.*;
import com.ternak.sapi.service.JenisHewanService;
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
@RequestMapping("/api/jenishewan")
public class JenisHewanController {
    private JenisHewanService jenishewanService = new JenisHewanService();

    @GetMapping
    public PagedResponse<JenisHewan> getJenisHewans(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "peternakID", defaultValue = "*") String peternakID,
            @RequestParam(value = "hewanID", defaultValue = "*") String hewanID,
            @RequestParam(value = "kandangID", defaultValue = "*") String kandangID) throws IOException {
        return jenishewanService.getAllJenisHewan(page, size, peternakID, hewanID, kandangID);
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity<byte[]> getFileFromHDFS(@PathVariable String fileName) {
        String uri = "hdfs://hadoop-master:9000/jenishewan/" + fileName;
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
    public ResponseEntity<?> createJenisHewan(@Valid @RequestBody JenisHewanRequest jenishewanRequest)
            throws IOException {
        try {
            JenisHewan jenishewan = jenishewanService.createJenisHewan(jenishewanRequest);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{idJenisHewan}")
                    .buildAndExpand(jenishewan.getIdJenisHewan()).toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponse(true, "Jenis Hewan Created Successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred."));
        }
    }

    @GetMapping("/{jenishewanId}")
    public DefaultResponse<JenisHewan> getJenisHewanById(@PathVariable String jenishewanId) throws IOException {
        return jenishewanService.getJenisHewanById(jenishewanId);
    }

    @PutMapping("/{jenishewanId}")
    public ResponseEntity<?> updateJenisHewan(@PathVariable String jenishewanId, @Valid @RequestBody JenisHewanRequest jenishewanRequest)
            throws IOException {
        JenisHewan jenisHewan = jenishewanService.updateJenisHewan(jenishewanId,jenishewanRequest);

        if (jenisHewan == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Jenis Hewan ID not found"));
        } else {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/jenishewanId}")
                    .buildAndExpand(jenisHewan.getIdJenisHewan()).toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponse(true, "Jenis Hewan Updated Successfully"));
        }

    }

    @DeleteMapping("/{jenishewanId}")
    public HttpStatus deleteJenisHewan(@PathVariable(value = "jenishewanId") String jenishewanId) throws IOException {
        jenishewanService.deleteJenisHewanById(jenishewanId);
        return HttpStatus.FORBIDDEN;
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkJenisHewan(@RequestBody List<JenisHewanRequest> jenishewanRequests) {
        try {
            jenishewanService.createBulkJenisHewan(jenishewanRequests);
            return ResponseEntity.ok(new ApiResponse(true, "All JenisHewan Created Successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to create bulk data: " + e.getMessage()));
        }
    }
}
