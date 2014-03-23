/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.api;

import com.sun.jersey.multipart.FormDataParam;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("")
public class FileResource {

    @Context
    private UriInfo context;

    @Context
    private HttpHeaders headers;

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/file/{path: .*}")
    public Response getFile(@PathParam("path") String path) throws IOException {
        FTPAdapterImpl adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, ApplicationConfig.username, ApplicationConfig.password);
        File file = adapter.retr(path);
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachement; filename = " + file.getName());
        return response.build();
    }

    @POST
    @Path("/file/{path:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(@PathParam("path") String path, @FormDataParam("file") InputStream received) {
        FTPAdapterImpl adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, ApplicationConfig.username, ApplicationConfig.password);
        System.out.println("PATH: " + path);
        try {
            if (path == null) {
            }
            System.out.println("received.toString() : " + received.toString());
            System.out.println("called POST " + path);
            if (!adapter.exists(path)) {
                return "failure: file does not exist";
            }
            if (adapter.isDirectory(path)) {
                return "failure: cannot modify directory";
            }
            DataInputStream dis = new DataInputStream(received);
            for (int i = 0; i < 4; i++) {
                dis.readLine();
            }
            adapter.stor(path, dis);
            return "success";
        } catch (IOException ex) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, null, ex);
            return "failure : " + ex.getMessage();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_OCTET_STREAM})
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/file/{path:.*}")
    public String put(@PathParam("path") String path, InputStream received) {
        FTPAdapterImpl adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, ApplicationConfig.username, ApplicationConfig.password);

        try {
            if (path == null || path.isEmpty()) {
                return "failure: the new file must be given a name!\n";
            }
            System.out.println("called PUT " + path);
            
            adapter.stor(path, received);
            return "success\n";
        } catch (IOException ex) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, null, ex);
            return "failure: " + ex.getMessage() + "\n";
        }
    }
}
