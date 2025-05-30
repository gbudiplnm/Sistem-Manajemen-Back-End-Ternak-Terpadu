package com.ternak.sapi.controller;

import com.ternak.sapi.config.PathConfig;
import com.ternak.sapi.model.Berita;
import com.ternak.sapi.model.Berita;
import com.ternak.sapi.payload.BeritaRequest;
import com.ternak.sapi.payload.ApiResponse;
import com.ternak.sapi.payload.DefaultResponse;
import com.ternak.sapi.payload.BeritaRequest;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.service.BeritaService;
import com.ternak.sapi.util.AppConstants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/berita")
public class BeritaController {
    private BeritaService beritaService = new BeritaService();

    @GetMapping
    public PagedResponse<Berita> getBeritas(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                    @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) throws IOException {
        return beritaService.getAllBerita(page, size);
    }
    
       
    @GetMapping("/file/{fileName}")
    public ResponseEntity<byte[]> getFileFromHDFS(@PathVariable String fileName) {
        String uri = "hdfs://hadoop-master:9000/berita/" + fileName;
      //  String uri = "hdfs://h-primary:6912/berita/" + fileName;
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
    public ResponseEntity<?> createBerita(@RequestPart(value = "file", required = false) MultipartFile file, @ModelAttribute BeritaRequest beritaRequest) throws IOException {

        if (file != null && !file.isEmpty()) {
            // upload file
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
                String hdfsDir = "hdfs://hadoop-master:9000/berita/" + newFileName + fileExtension;
//                   String uri = "hdfs://h-primary:6912";
//                String hdfsDir = "hdfs://h-primary:6912/berita/" + newFileName + fileExtension;
//                 String uri = "localhost:8081/api";
//                String hdfsDir = "localhost:8081/api/berita/" + newFileName + fileExtension;
                Configuration configuration = new Configuration();
                FileSystem fs = FileSystem.get(URI.create(uri), configuration);
                fs.copyFromLocalFile(new Path(localPath), new Path(hdfsDir));
                String savePath = "file/"+ newFileName + fileExtension ;

                newFile.delete();
                Berita berita = beritaService.createBerita(beritaRequest, savePath);

                if(berita == null){
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Please check relational ID"));
                }else{
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{beritaId}")
                            .buildAndExpand(berita.getIdBerita()).toUri();

                    return ResponseEntity.created(location)
                            .body(new ApiResponse(true, "Berita Created Successfully"));
                }
            } catch (IOException e) {
                // Penanganan kesalahan saat menyimpan file
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Cannot Upload File into Hadoop"));
            }
        }else{
            // Tidak ada input file
            try {
                Berita berita = beritaService.createBerita(beritaRequest, "");

                if(berita == null){
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Please check relational ID"));
                }else{
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{beritaId}")
                            .buildAndExpand(berita.getIdBerita()).toUri();

                    return ResponseEntity.created(location)
                            .body(new ApiResponse(true, "Berita Created Successfully"));
                }
            } catch (IOException e) {
                // Penanganan kesalahan saat menyimpan file
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Cannot Upload File into Hadoop"));
            }
        }



    }

    @GetMapping("/{beritaId}")
    public DefaultResponse<Berita> getBeritaById(@PathVariable String beritaId) throws IOException {
        return beritaService.getBeritaById(beritaId);
    }


    @PutMapping("/{beritaId}")
    public ResponseEntity<?> updateBerita(@PathVariable String beritaId,
                                          @RequestParam("file") MultipartFile file, @ModelAttribute BeritaRequest beritaRequest) throws IOException {
        // upload file
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
            String hdfsDir = "hdfs://hadoop-master:9000/berita/" + newFileName + fileExtension;
//   String uri = "hdfs://h-primary:6912";
//                String hdfsDir = "hdfs://h-primary:6912/berita/" + newFileName + fileExtension;
            Configuration configuration = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(uri), configuration);
            fs.copyFromLocalFile(new Path(localPath), new Path(hdfsDir));
            String savePath = "file/"+ newFileName + fileExtension ;

            newFile.delete();
            Berita berita = beritaService.updateBerita(beritaId, beritaRequest, savePath);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{beritaId}")
                    .buildAndExpand(berita.getIdBerita()).toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponse(true, "Berita Updated Successfully"));
        } catch (IOException e) {
            // Penanganan kesalahan saat menyimpan file
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Cannot Upload File into Hadoop"));
        }


    }

    @DeleteMapping("/{beritaId}")
    public HttpStatus deleteBerita(@PathVariable (value = "beritaId") String beritaId) throws IOException {
        beritaService.deleteBeritaById(beritaId);
        return HttpStatus.FORBIDDEN;
    }
}
