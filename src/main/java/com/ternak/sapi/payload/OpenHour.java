package com.ternak.sapi.payload;

import lombok.Data;

@Data
public class OpenHour {
    private String hari;
    private boolean aktif;
    private String jamBuka;
    private String jamTutup;
}
