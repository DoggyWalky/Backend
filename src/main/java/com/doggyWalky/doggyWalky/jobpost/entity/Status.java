package com.doggyWalky.doggyWalky.jobpost.entity;

public enum Status {
    WAITING("대기","Waiting"),
    RESERVED("예약","Reserved");

    private final String krStatus;
    private final String enStatus;

    Status(String krStatus,String enStatus){
        this.krStatus = krStatus;
        this.enStatus = enStatus;
    }

    public String getKrStatus() {
        return krStatus;
    }

    public String getEnStatus() {
        return enStatus;
    }
}
