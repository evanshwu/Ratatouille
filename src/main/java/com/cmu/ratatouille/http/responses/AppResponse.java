package com.cmu.ratatouille.http.responses;

public class AppResponse {
    public boolean success = true;
    public Object data;
    public int httpStatusCode = 200;

    public AppResponse(Object dataParam) {
        this.data = dataParam;
    }

    public AppResponse() {
    }
}