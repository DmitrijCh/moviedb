package com.springbootapp.moviedb.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private final String result;
    private final String error;

    public String getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public ApiResponse(String result, String error) {
        this.result = result;
        this.error = error;
    }

    public static ApiResponse ok() {
        return new ApiResponse("ok", null);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse("error", message);
    }
}
