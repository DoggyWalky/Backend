package com.doggyWalky.doggyWalky.security.redis;

public interface TokenStorageService {

    String getRefreshToken(String key);


    void setRefreshToken(String key,String value);

    void removeRefreshToken(String key);

}
