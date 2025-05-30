package com.ternak.sapi.controller;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ternak.sapi.model.PusatEvakuasi;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.payload.PusatEvakuasiRequest;
import com.ternak.sapi.queries.UniversalQueries;
import com.ternak.sapi.service.PusatEvakuasiService;

@RestController
@RequestMapping("/api/pusat-evakuasi")
public class PusatEvakuasiController {

    private PusatEvakuasiService pusatEvakuasiService = new PusatEvakuasiService();

    @GetMapping("/list")
    public PagedResponse<PusatEvakuasi> getPusatEvakuasi(@RequestParam(required = false) UniversalQueries query) throws IOException {
        return this.pusatEvakuasiService.getAllPusatEvakuasi(query);
    }

    @GetMapping("/detail/{id}")
    public PusatEvakuasi getAllPusatEvakuasi(@PathVariable(required = false) String id) throws IOException {
        return this.pusatEvakuasiService.getPusatEvakuasiById(id);
    }

    @PostMapping("/create")
    public PusatEvakuasi savePusatEvakuasi(@RequestBody(required = false) PusatEvakuasiRequest pusatEvakuasi) throws IOException {
        return this.pusatEvakuasiService.savePusatEvakuasi(pusatEvakuasi);
    }

    @PutMapping("/edit/{id}")
    public PusatEvakuasi updatePusatEvakuasi(@PathVariable(required = false) String id, @RequestBody(required = false) PusatEvakuasiRequest pusatEvakuasi) throws IOException {
        return this.pusatEvakuasiService.updatePusatEvakuasi(id, pusatEvakuasi);
    }
}
