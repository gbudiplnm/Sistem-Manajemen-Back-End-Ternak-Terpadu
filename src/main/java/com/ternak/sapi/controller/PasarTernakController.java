package com.ternak.sapi.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.ternak.sapi.model.PasarTernak;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PasarTernakRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.service.PasarTernakService;

public class PasarTernakController {
    
    private PasarTernakService pasarTernakService = new PasarTernakService();

    @GetMapping("/list")
    public PagedResponse<PasarTernak> getPasarTernak(@RequestParam(required = false) UniversalQueries query) throws IOException {
        return this.pasarTernakService.getAllPasarTernak(query);
    }

    @GetMapping("/detail/{id}")
    public PasarTernak getAllPasarTernak(@PathVariable(required = false) String id) throws IOException {
        return this.pasarTernakService.getPasarTernakById(id);
    }

    @PostMapping("/create")
    public PasarTernak savePasarTernak(@RequestBody(required = false) PasarTernakRequest pasarTernak) throws IOException {
        return this.pasarTernakService.savePasarTernak(pasarTernak);
    }

    @PutMapping("/edit/{id}")
    public PasarTernak updatePasarTernak(@PathVariable(required = false) String id, @RequestBody(required = false) PasarTernakRequest pasarTernak) throws IOException {
        return this.pasarTernakService.updatePasarTernak(id, pasarTernak);
    }
}
