package com.doggyWalky.doggyWalky.dog.entity;

public enum DogSize {
    SMALL("소형견"),
    MEDIUM("중형견"),
    LARGE("대형견");

    private final String koreanName;

    DogSize(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

}
