package com.ternak.sapi.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ternak.sapi.model.PasarTernak;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PasarTernakEditRequest;
import com.ternak.sapi.payload.PasarTernakRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.service.PasarTernakService;
import com.ternak.sapi.util.AppUtility;

@RestController
@RequestMapping("/api/pasar-ternak")
public class PasarTernakController {

    private PasarTernakService pasarTernakService = new PasarTernakService();

    @GetMapping("/list")
    public PagedResponse<PasarTernak> getPasarTernak(@ModelAttribute UniversalQueries query) throws IOException {
        return this.pasarTernakService.getAllPasarTernak(query);
    }

    @GetMapping("/detail/{id}")
    public PasarTernak getAllPasarTernak(@PathVariable(required = false) String id) throws IOException {
        return this.pasarTernakService.getPasarTernakById(id);
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public PasarTernak savePasarTernak(@ModelAttribute PasarTernakRequest pasarTernak) throws IOException {
        String Berhasil = "Berhasil";
        return this.pasarTernakService.savePasarTernak(pasarTernak);
    }

    @PostMapping(value = "/coba", consumes = "multipart/form-data")
    public String coba(@ModelAttribute PasarTernakRequest pasarTernak) throws IOException {
        String Berhasil = "Berhasil";
        return Berhasil;
    }

    @PutMapping("/edit/{id}")
    public PasarTernak updatePasarTernak(@PathVariable(required = false) String id,
            @ModelAttribute PasarTernakEditRequest pasarTernak) throws IOException {
        return this.pasarTernakService.updatePasarTernak(id, pasarTernak);
    }

    @PostMapping(value = "/upload-file", consumes = "multipart/form-data")
    public String[] uploadFile(@RequestPart("files") MultipartFile[] files, @RequestParam(required = false) String id)
            throws IOException {
        AppUtility.isFileExtensionCorrect(files, "jpg", "jpeg", "png");
        String[] paths = pasarTernakService.uploadFile(files, id);
        return paths;
    }

    @DeleteMapping(value = "/delete-file")
    public boolean deleteFile(@RequestParam("path") String path, @RequestParam(required = false) String id)
            throws IOException {
        return pasarTernakService.deleteFile(path, id);
    }

}
