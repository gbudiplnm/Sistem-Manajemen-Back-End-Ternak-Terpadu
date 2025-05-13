package com.ternak.sapi.queries;

import com.ternak.sapi.util.AppConstants;

import lombok.Data;

@Data
public class UniversalQueries {
    private int page = Integer.parseInt(AppConstants.DEFAULT_PAGE_NUMBER);
    private int size = Integer.parseInt(AppConstants.DEFAULT_PAGE_SIZE);

}
