package com.ternak.sapi.queries;

import com.ternak.sapi.util.AppConstants;


public class LahanHijauQuery {
    private int page = Integer.parseInt(AppConstants.DEFAULT_PAGE_NUMBER);
    private int size = Integer.parseInt(AppConstants.DEFAULT_PAGE_SIZE);
    private String peternakID = "*";
    private String petugasInputID = "*";
    private String petugasReviewID = "*";

    public void setPage(int page) {
        this.page = page;
    }

    public void setPeternakID(String peternakID) {
        this.peternakID = peternakID;
    }

    public void setPetugasInputID(String petugasInputID) {
        this.petugasInputID = petugasInputID;
    }

    public void setPetugasReviewID(String petugasReviewID) {
        this.petugasReviewID = petugasReviewID;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public String getPeternakID() {
        return peternakID;
    }

    public String getPetugasInputID() {
        return petugasInputID;
    }

    public String getPetugasReviewID() {
        return petugasReviewID;
    }

    public int getSize() {
        return size;
    }
}
