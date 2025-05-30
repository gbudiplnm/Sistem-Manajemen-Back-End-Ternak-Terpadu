package com.ternak.sapi.controller;

import java.io.IOException;

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

import com.ternak.sapi.model.Puskesmas;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PuskesmasRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.service.PuskesmasService;

@RestController
@RequestMapping("/api/puskesmas")
public class PuskesmasController {
    
    private PuskesmasService puskesmasService = new PuskesmasService();

    @GetMapping("/list")
    public PagedResponse<Puskesmas> getPuskesmas(@ModelAttribute UniversalQueries query) throws IOException {
        return this.puskesmasService.getAllPuskesmas(query);
    }

    @GetMapping("/detail/{id}")
    public Puskesmas getAllPuskesmas(@PathVariable(required = false) String id) throws IOException {
        return this.puskesmasService.getPuskesmasById(id);
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public Puskesmas savePuskesmas(@ModelAttribute PuskesmasRequest puskesmas) throws IOException {
        return this.puskesmasService.savePuskesmas(puskesmas);
    }

    @PutMapping("/edit/{id}")
    public Puskesmas updatePuskesmas(@PathVariable(required = false) String id, @RequestBody(required = false) PuskesmasRequest puskesmas) throws IOException {
        return this.puskesmasService.updatePuskesmas(id, puskesmas);
    }
}
