package com.doggyWalky.doggyWalky.jobpost.entity;

import jakarta.persistence.Enumerated;


public enum WalkingProcessStatus {

    PREWALK("산책전"), WALKING("산책중"), POSTWALK("산책끝");

    String status;

    WalkingProcessStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
