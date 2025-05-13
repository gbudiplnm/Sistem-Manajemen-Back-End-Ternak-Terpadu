package com.ternak.sapi.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ternak.sapi.model.LahanHijau;
import com.ternak.sapi.payload.LahanHijauRequest;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.queries.LahanHijauQuery;
import com.ternak.sapi.service.KelahiranService;
import com.ternak.sapi.service.LahanHijauService;
import com.ternak.sapi.util.AppConstants;

@RestController
@RequestMapping("/api/lahan-hijau")
public class LahanHijauController {
    private LahanHijauService lahanHijauService = new LahanHijauService();

    @GetMapping
    public PagedResponse<LahanHijau> getAllLahanHijauUser(@RequestParam(required = false) LahanHijauQuery query)
            throws IOException {
        return this.lahanHijauService.getAllLahanHijauUser(query);
    }

    @GetMapping("/admin")
    public PagedResponse<LahanHijau> getAllLahanHijauAdmin(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) throws IOException {
                LahanHijauQuery query = new LahanHijauQuery();
                query.setPage(page);
                query.setSize(size);
        return this.lahanHijauService.getAllLahanHijau(query);
    }

    @PostMapping("/admin/create")
    public LahanHijau saveLahanHijau(@RequestBody(required = false) LahanHijauRequest lahanHijau) throws IOException {
        return this.lahanHijauService.saveLahanHijauAdmin(lahanHijau);
    }

    @PostMapping("/user/create")
    public LahanHijau saveLahanHijauUser(@RequestBody(required = false) LahanHijauRequest lahanHijau)
            throws IOException {
        return this.lahanHijauService.saveLahanHijauUser(lahanHijau);
    }

    @GetMapping("/detail/{id}")
    public LahanHijau getLahanHijauById(@RequestParam(required = false) String id) throws IOException {
        return this.lahanHijauService.getLahanHijauById(id);
    }

    @PutMapping("/admin/edit/{id}")
    public LahanHijau updateLahanHijauAdmin(@RequestParam(required = false) String id,
            @RequestBody(required = false) LahanHijauRequest lahanHijau) throws IOException {
        return this.lahanHijauService.updateLahanHijauAdmin(id, lahanHijau);
    }

}
