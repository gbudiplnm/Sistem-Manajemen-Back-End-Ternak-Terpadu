package com.ternak.sapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ternak.sapi.service.HadoopFileService;

@RestController
@RequestMapping("/api/file")
public class FileController {
    
    @Autowired
    private HadoopFileService hadoopFileService;

    @GetMapping("/get")
    public ResponseEntity<byte[]> getFile(@RequestParam String fileName) {
        return hadoopFileService.getFileFromHDFS(fileName);
    }

    @DeleteMapping("/delete-multiple")
    public boolean[] deleteMultipleFiles(@RequestParam String[] fileNames) {
        return hadoopFileService.deleteFiles(fileNames);
    }

}
