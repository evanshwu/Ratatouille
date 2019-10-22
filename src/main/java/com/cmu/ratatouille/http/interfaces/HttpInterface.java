package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.exceptions.HttpInternalServerException;
import com.cmu.ratatouille.utils.AppLogger;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONException;

import javax.ws.rs.WebApplicationException;

public class HttpInterface extends ResourceConfig {
    protected WebApplicationException handleException(String message, Exception e){
        if(e instanceof JSONException)
            return new HttpBadRequestException(-1, "Bad request data provided: " + e.getMessage());
        if (e instanceof AppException)
            return ((AppException) e).getHttpException();

        AppLogger.error(message, e);
        return new HttpInternalServerException(-1);
    }
}

