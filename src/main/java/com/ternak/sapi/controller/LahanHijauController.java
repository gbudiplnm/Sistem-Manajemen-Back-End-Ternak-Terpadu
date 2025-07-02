package com.ternak.sapi.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

import com.ternak.sapi.exception.ApiException;
import com.ternak.sapi.exception.BadRequestException;
import com.ternak.sapi.model.LahanHijau;
import com.ternak.sapi.payload.LahanHijauEditRequest;
import com.ternak.sapi.payload.LahanHijauRequest;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.UserSummary;
import com.ternak.sapi.queries.LahanHijauQuery;
import com.ternak.sapi.security.CurrentUser;
import com.ternak.sapi.security.UserPrincipal;
import com.ternak.sapi.service.LahanHijauService;
import com.ternak.sapi.util.AppConstants;
import com.ternak.sapi.util.AppUtility;
import com.ternak.sapi.util.annotations.BodyField;

import lombok.Data;

@RestController
@RequestMapping("/api/lahan-hijau")
public class LahanHijauController {

    @Autowired(required = true)
    private LahanHijauService lahanHijauService;

    @Value("${TEST}")
    private String test;

    @Data
    private class CobaClass {
        String nama;
        String alamat;
    }

    
    @PostMapping("/testt")
    public String test(@RequestPart(required = false) CobaClass cobaClass) {
        System.out.println(cobaClass);
        return test;
    }

    @GetMapping
    public PagedResponse<LahanHijau> getAllLahanHijauUser(@RequestParam(required = false) LahanHijauQuery query)
            throws IOException {
        return this.lahanHijauService.getAllLahanHijauUser(query);
    }

    @GetMapping("/petugas")
    public PagedResponse<LahanHijau> getAllLahanHijauAdmin(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) throws IOException {
        LahanHijauQuery query = new LahanHijauQuery();
        query.setPage(page);
        query.setSize(size);
        return this.lahanHijauService.getAllLahanHijau(query);
    }

    @PostMapping(value = "/upload-file", consumes = "multipart/form-data")
    public String[] uploadFile(@RequestPart("files") MultipartFile[] files,@RequestParam(required = false) String id) throws IOException {
        AppUtility.isFileExtensionCorrect(files,"jpg","jpeg","png");
        String[] paths = lahanHijauService.uploadFile(files,id);
        return paths;
    }

    @DeleteMapping(value="/delete-file")
    public boolean deleteFile(@RequestParam("path") String path,@RequestParam(required = false) String id) throws IOException {
        return lahanHijauService.deleteFile(path,id);
    }

    @PostMapping(value = "/petugas/create", consumes = "multipart/form-data")
    public LahanHijau saveLahanHijau(@ModelAttribute LahanHijauRequest lahanHijau) throws IOException {
        return this.lahanHijauService.saveLahanHijauAdmin(lahanHijau);
    }

    @PutMapping("/petugas/terima/{id}")
    public LahanHijau terimaLahanHijauPetugas(@BodyField("catatan") String catatan, @CurrentUser UserPrincipal currentUser,
            @PathVariable(required = false) String id) throws IOException {
        UserSummary user = new UserSummary(currentUser);
        String role = user.getRole();
        if (!role.equalsIgnoreCase("ROLE_PETUGAS")) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Anda Tidak Memiliki Akses");
        }
        return this.lahanHijauService.terimaLahanHijauPetugas(id, user, catatan);
    }

    @PutMapping("/petugas/tolak/{id}")
    public LahanHijau tolakLahanHijauPetugas(@BodyField("catatan") String catatan, @CurrentUser UserPrincipal currentUser,
            @PathVariable(required = false) String id) throws IOException {
        UserSummary user = new UserSummary(currentUser);
        String role = user.getRole();
        if (!role.equalsIgnoreCase("ROLE_PETUGAS")) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Anda Tidak Memiliki Akses");
        }
        return this.lahanHijauService.tolakLahanHijauPetugas(id, user, catatan);
    }

    @PostMapping(value = "/peternak/create", consumes = "multipart/form-data")
    public LahanHijau saveLahanHijauUser(@ModelAttribute LahanHijauRequest lahanHijau)
            throws IOException {
        return this.lahanHijauService.saveLahanHijauUser(lahanHijau);
    }

    @GetMapping("/detail")
    public LahanHijau getLahanHijauById(@RequestParam(required = false) String id) throws IOException {
        return this.lahanHijauService.getLahanHijauById(id);
    }

    @PutMapping("/petugas/edit/{id}")
    public LahanHijau updateLahanHijauAdmin(@PathVariable(required = false) String id,
            @Valid @ModelAttribute LahanHijauEditRequest lahanHijau) throws IOException {
        return this.lahanHijauService.updateLahanHijauAdmin(id, lahanHijau);
    }

}
