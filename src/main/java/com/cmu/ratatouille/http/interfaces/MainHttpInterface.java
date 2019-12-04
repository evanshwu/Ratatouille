package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.responses.AppResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.glassfish.jersey.server.ResourceConfig;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.TreeMap;


@Path("")

public class MainHttpInterface extends ResourceConfig {

    private ObjectWriter ow;


    public MainHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @OPTIONS
    @PermitAll
    public Response options() {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * GET /
     *
     * @return {  Version, Release Date, Environment, Mail Prefix, Up Since, Warranty Conditions }
     */

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public Object VersionGet() {
        TreeMap<String,String> version = new TreeMap<>();
        version.put("Release Version", "1.0");
        return new AppResponse(version);
    }


}