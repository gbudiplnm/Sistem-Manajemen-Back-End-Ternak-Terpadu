package com.ternak.sapi.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ternak.sapi.payload.AllLocationResponse;
import com.ternak.sapi.service.LocationService;

@RestController
@RequestMapping("/api/location")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping("/all")
    public List<AllLocationResponse> getAllLocation() throws IOException {
        return locationService.getAllLocation();
    }
}
