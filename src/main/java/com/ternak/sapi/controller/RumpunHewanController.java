package com.ternak.sapi.controller;

import com.ternak.sapi.config.PathConfig;
import com.ternak.sapi.model.RumpunHewan;
import com.ternak.sapi.model.Peternak;
import com.ternak.sapi.model.TujuanPemeliharaan;
import com.ternak.sapi.payload.*;
import com.ternak.sapi.service.RumpunHewanService;
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
@RequestMapping("/api/rumpunhewan")
public class RumpunHewanController {
    private RumpunHewanService rumpunhewanService = new RumpunHewanService();

    @GetMapping
    public PagedResponse<RumpunHewan> getRumpunHewans(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) throws IOException {
        return rumpunhewanService.getAllRumpunHewan(page, size);
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity<byte[]> getFileFromHDFS(@PathVariable String fileName) {
        String uri = "hdfs://hadoop-master:9000/rumpunhewan/" + fileName;
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
    public ResponseEntity<?> createRumpunHewan(@Valid @RequestBody RumpunHewanRequest rumpunhewanRequest) {
        try {
            RumpunHewan rumpunhewan = rumpunhewanService.createRumpunHewan(rumpunhewanRequest);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{idRumpunHewan}")
                    .buildAndExpand(rumpunhewan.getIdRumpunHewan()).toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponse(true, "Rumpun Hewan Created Successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred."));
        }
    }

    @GetMapping("/{rumpunhewanId}")
    public DefaultResponse<RumpunHewan> getRumpunHewanById(@PathVariable String rumpunhewanId) throws IOException {
        return rumpunhewanService.getRumpunHewanById(rumpunhewanId);
    }

    @PutMapping("/{rumpunhewanId}")
    public ResponseEntity<?> updateRumpunHewan(@PathVariable String rumpunhewanId,
                                                      @Valid @RequestBody RumpunHewanRequest rumpunHewanRequest) throws IOException {
        RumpunHewan rumpunHewan = rumpunhewanService.updateRumpunHewan(rumpunhewanId, rumpunHewanRequest);

        if (rumpunHewan == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Rumpun ID not found"));
        } else {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{rumpunhewanId}")
                    .buildAndExpand(rumpunHewan.getIdRumpunHewan()).toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponse(true, "Tujuan Updated Successfully"));
        }
    }

    @DeleteMapping("/{rumpunhewanId}")
    public HttpStatus deleteRumpunHewan(@PathVariable(value = "rumpunhewanId") String rumpunhewanId)
            throws IOException {
        rumpunhewanService.deleteRumpunHewanById(rumpunhewanId);
        return HttpStatus.FORBIDDEN;
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkRumpunHewan(@RequestBody List<RumpunHewanRequest> rumpunhewanRequests) {
        try {
            rumpunhewanService.createBulkRumpunHewan(rumpunhewanRequests);
            return ResponseEntity.ok(new ApiResponse(true, "Bulk Rumpun Hewan Created Successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred."));
        }
    }
}
