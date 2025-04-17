package com.ternak.sapi.service;

import java.io.IOException;
import java.util.List;

import com.ternak.sapi.model.LahanHijau;
import com.ternak.sapi.payload.PagedResponse;
import com.ternak.sapi.queries.LahanHijauQuery;
import com.ternak.sapi.repository.LahanHijauRepository;
import com.ternak.sapi.util.AppUtility;

public class LahanHijauService {
    private LahanHijauRepository lahanHijauRepository = new LahanHijauRepository();

    public PagedResponse<LahanHijau> getAllLahanHijau(LahanHijauQuery query) throws IOException {
        AppUtility.validatePageNumberAndSize(query.getPage(), query.getSize());
        List<LahanHijau> lahanHijauList = lahanHijauRepository.findAll(query.getSize());
        return new PagedResponse<>(lahanHijauList, lahanHijauList.size(), "Successfully get data", 200);
    }
}
