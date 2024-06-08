package com.doggyWalky.doggyWalky.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // COMMON
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "Permission is invalid"),
    ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "Access forbidden"),
    STATUS_VALUE_NOT_FOUND(HttpStatus.NOT_FOUND, "status value not founded"),
    INVALID_VALUE(HttpStatus.FORBIDDEN, "Invalid value"),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "Invalid enum value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    CRYPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Crypt error"),

    // AUTHORITY

    AUTHORITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Authority not founded"),

    // TOKEN
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"User Authentication is failed"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token is invalid"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "RefreshToken is invalid"),

    // LOGIN
    INVALID_SOCIAL_LOGIN_TYPE(HttpStatus.BAD_REQUEST, "Unsupported or invalid social login type"),

    // MEMBER
    INCORRECT_FORMAT_NICKNAME(HttpStatus.UNPROCESSABLE_ENTITY, "Nickname format is Incorrect"),
    INCORRECT_FORMAT_DESCRIPTION(HttpStatus.UNPROCESSABLE_ENTITY, "Description format is Incorrect"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),


    // File
    INVALID_FILE_TYPE(HttpStatus.FORBIDDEN, "Invalid file type"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File not founded"),
    FILE_INFO_EXISTS(HttpStatus.CONFLICT, "File Info exists"),
    MAX_FILE_SIZE_10MB(HttpStatus.BAD_REQUEST, "Max file size 10MB"),
    FILE_STORAGE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "File storage failed"),

    // JobPost
    JOBPOST_NOT_FOUND(HttpStatus.NOT_FOUND, "JobPost not founded"),
    NOT_JOBPOST_WRITER(HttpStatus.FORBIDDEN, "Not JobPost Writer"),

    // Report
    INCORRECT_FORMAT_REPORTCONTENT(HttpStatus.UNPROCESSABLE_ENTITY, "ReportContent format is Incorrect"),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "Report not founded"),

    // Apply
    INCORRECT_MATCH_WRITER(HttpStatus.BAD_REQUEST, "Incorrect Apply"),
    ALREADY_REGISTERED_APPLY(HttpStatus.BAD_REQUEST, "Already register Apply"),
    NOT_APPLY_SELF(HttpStatus.BAD_REQUEST, "Do not apply yourself"),

    INVALID_APPLY_PERMISSION(HttpStatus.FORBIDDEN, "Invalid Permission For Apply"),

    ACCEPT_APPLY_EXISTS(HttpStatus.CONFLICT, "There is already a accepted application"),

    //Dog
    DOG_NOT_FOUND(HttpStatus.NOT_FOUND, "Dog not founded");

    private HttpStatus status;
    private String message;
}
