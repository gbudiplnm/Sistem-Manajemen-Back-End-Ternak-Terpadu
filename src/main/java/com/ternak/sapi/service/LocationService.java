package com.ternak.sapi.service;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ternak.sapi.model.LahanHijau;
import com.ternak.sapi.model.PasarTernak;
import com.ternak.sapi.model.PusatEvakuasi;
import com.ternak.sapi.model.Puskesmas;
import com.ternak.sapi.payload.AllLocationResponse;
import com.ternak.sapi.payload.TableNameAndNameColumn;
import com.ternak.sapi.repository.LocationRepository;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public List<AllLocationResponse> getAllLocation() throws IOException{
        List<TableNameAndNameColumn> classes = Arrays.asList(
                new TableNameAndNameColumn("lahanHijauDev", "namaLahanHijau","idLahan",LahanHijau.class),
                new TableNameAndNameColumn("pasarTernakDev", "namaPasar","idPasar",PasarTernak.class),
                new TableNameAndNameColumn("puskesmasDev", "namaPuskesmas","idPuskesmas",Puskesmas.class),
                new TableNameAndNameColumn("pusatEvakuasiDev", "namaPusatEvakuasi","idPusatEvakuasi",PusatEvakuasi.class)
        );
        List<AllLocationResponse> responses = new ArrayList<>();
        for(TableNameAndNameColumn cls : classes){
            List<AllLocationResponse> response = locationRepository.getAllLocation(cls, cls.getClassObject());
            responses.addAll(response);
        }
        return responses;
    }
}
