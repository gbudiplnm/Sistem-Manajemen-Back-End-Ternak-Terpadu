package com.ternak.sapi.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ternak.sapi.model.PusatEvakuasi;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PusatEvakuasiEditRequest;
import com.ternak.sapi.payload.PusatEvakuasiRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.service.PusatEvakuasiService;
import com.ternak.sapi.util.AppUtility;

@RestController
@RequestMapping("/api/pusat-evakuasi")
public class PusatEvakuasiController {

    private PusatEvakuasiService pusatEvakuasiService = new PusatEvakuasiService();

    @GetMapping("/list")
    public PagedResponse<PusatEvakuasi> getPusatEvakuasi(@ModelAttribute UniversalQueries query) throws IOException {
        return this.pusatEvakuasiService.getAllPusatEvakuasi(query);
    }

    @GetMapping("/detail/{id}")
    public PusatEvakuasi getAllPusatEvakuasi(@PathVariable(required = false) String id) throws IOException {
        return this.pusatEvakuasiService.getPusatEvakuasiById(id);
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public PusatEvakuasi savePusatEvakuasi(@ModelAttribute PusatEvakuasiRequest pusatEvakuasi) throws IOException {
        return this.pusatEvakuasiService.savePusatEvakuasi(pusatEvakuasi);
    }

    @PutMapping("/edit/{id}")
    public PusatEvakuasi updatePusatEvakuasi(@PathVariable(required = false) String id,
            @ModelAttribute PusatEvakuasiEditRequest pusatEvakuasi) throws IOException {
        return this.pusatEvakuasiService.updatePusatEvakuasi(id, pusatEvakuasi);
    }

    @PostMapping(value = "/upload-file", consumes = "multipart/form-data")
    public String[] uploadFile(@RequestPart("files") MultipartFile[] files, @RequestParam(required = false) String id)
            throws IOException {
        AppUtility.isFileExtensionCorrect(files, "jpg", "jpeg", "png");
        String[] paths = pusatEvakuasiService.uploadFile(files, id);
        return paths;
    }

    @DeleteMapping(value = "/delete-file")
    public boolean deleteFile(@RequestParam("path") String path, @RequestParam(required = false) String id)
            throws IOException {
        return pusatEvakuasiService.deleteFile(path, id);
    }

}
