package com.ternak.sapi.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ternak.sapi.model.LahanHijau;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.queries.LahanHijauQuery;
import com.ternak.sapi.service.KelahiranService;
import com.ternak.sapi.service.LahanHijauService;

@RestController
@RequestMapping("/api/lahan-hijau")
public class LahanHijauController {
    private LahanHijauService lahanHijauService = new LahanHijauService();
    @GetMapping
    public PagedResponse<LahanHijau> getAllLahanHijau(LahanHijauQuery query) throws IOException {
        return this.lahanHijauService.getAllLahanHijau(query);
    }

    // @PostMapping
    // public LahanHijau saveLahanHijau(@RequestParam(required = false) LahanHijau lahanHijau) throws IOException {
    //     return this.lahanHijauService.saveLahanHijau(lahanHijau);
    // }

}
