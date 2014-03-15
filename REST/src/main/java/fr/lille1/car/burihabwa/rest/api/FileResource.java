/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.api;

import java.io.File;
import java.io.IOException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * REST Web Service
 *
 * @author dorian
 */
@Path("/file")
public class FileResource {

    @Context
    private UriInfo context;

    @Context
    private HttpHeaders headers;

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{path: .*}")
    public Response getFile(@PathParam("path") String path) throws IOException {
        FTPAdapterImpl adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, ApplicationConfig.username, ApplicationConfig.password);
        File file = adapter.retr(path);
        adapter.close();
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachement; filename = " + file.getName());
        return response.build();
    }
}
