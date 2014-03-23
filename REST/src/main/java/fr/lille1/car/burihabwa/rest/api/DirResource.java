/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.api;

import fr.lille1.car.burihabwa.rest.utils.FTPAdapterImpl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.apache.commons.net.ftp.FTPFile;

/**
 * REST Web Service
 *
 * @author dorian
 */
@Path("/dir")
public class DirResource {

    @Context
    private UriInfo context;

    @Context
    private HttpHeaders headers;

    /**
     * Creates a new instance of DirResource
     */
    public DirResource() {
    }

    @GET
    @Produces("text/html")
    public String list() throws IOException {
        return list(null);
    }

    /**
     * Retrieves representation of an instance of
     * fr.lille1.car.burihabwa.rest.DirResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/html")
    @Path("{path: .*}")
    public String list(@PathParam("path") String path) throws IOException {
        FTPAdapterImpl adapter = null;
        FTPFile[] files = null;
        String content = "<!DOCTYPE html>\n";
        content += "<html>\n";
        content += "<head>\n";
        content += "\t<title>/dir/</title>\n";
        content += "</head>\n<body>\n";
        content += "<h1>" + "</h1>\n";
        try {
            adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, ApplicationConfig.username, ApplicationConfig.password);
            files = adapter.list(path);
        } catch (IOException ex) {
            Logger.getLogger(DirResource.class.getName()).log(Level.SEVERE, null, ex);
            content += "\t<h1>An error occured</h1>\n";
            content += "\t<p>" + ex.getMessage() + "</p>\n";
            content += "</body>\n";
            content += "</html>\n";
            return content;
        } finally {
            if (adapter != null) {
                adapter.close();
            }
        }
        content += "\t<h1>You asked for " + path + "</h1>\n";
        content += "\t<table>\n";
        content += "\t\t<thead>\n";
        content += "\t\t\t<tr>\n";
        content += "\t\t\t\t<th>Name</th>\n";
        content += "\t\t\t\t<th>DELETE</th>\n";
        content += "\t\t\t</tr>\n";
        content += "\t\t<thead>\n";
        content += "\t\t<tbody>\n";
        for (FTPFile file : files) {
            content += "\t\t\t<tr>\n";
            content += "\t\t\t\t<td>";
            if (file.isDirectory()) {
                content += "<a";
                content += " title = \"" + file.getName() + "\"";
                content += " href = \"";
                if (path != null) {
                    content += context.getAbsolutePath().getPath() + "/";
                }
                content += file.getName() + "\">" + file.getName() + "</a>";
            } else {
                content += "<a";
                content += " title = \"" + file.getName() + "\"";
                content += " href = \"/REST/api/file/";
                if (path != null) {
                    content += context.getAbsolutePath().getPath() + "/";
                }
                content += file.getName() + "\">" + file.getName() + "</a>";
            }
            content += "</td>\n";
            content += "\t\t\t\t<td><a class = \"delete\" href = \"\">DELETE</a></td>\n";
            content += "\t\t\t</tr>\n";
        }
        content += "\t\t<tbody>\n";
        content += "\t</table>\n";
        content += "</body>\n";
        content += "</html>\n";
        return content;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{path: .*}")
    public String listJSON(@PathParam("path") String path) throws IOException {
        FTPAdapterImpl adapter = null;
        FTPFile[] files = null;
        String content = "[";
        try {
            adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, ApplicationConfig.username, ApplicationConfig.password);
            files = adapter.list(path);
        } catch (IOException ex) {
            Logger.getLogger(DirResource.class.getName()).log(Level.SEVERE, null, ex);
            content += "An error occured]";
            return content;
        } finally {
            if (adapter != null) {
                adapter.close();
            }
        }
        for (FTPFile file : files) {
            content += "{ ";
            content += "name: " + file.getName() + ", ";
            content += "size: " + file.getSize() + ", ";
            content += "type: " + (file.isDirectory() ? "directory" : "file");
            content += " }, ";
        }
        content += "]";
        return content;
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public String delete(@PathParam("path") String path) {
        Response.ResponseBuilder response = Response.ok();
        FTPAdapterImpl adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, ApplicationConfig.username, ApplicationConfig.password);
        boolean result = false;
        try {
            result = adapter.delete(path);
        } catch (IOException ex) {
            Logger.getLogger(DirResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (result) {
            response = response.status(Response.Status.OK);
            return "successful DELETE of " + path + "!\n";
        } else {
            response = response.status(Response.Status.NOT_FOUND);
            return "unsuccesful DELETE of " + path + "!\n";
        }
        //return response.build();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(@PathParam("path") String path) {
        boolean result = false;
        if (result) {
            return "successful POST of " + path + "!\n";
        }
        return "unsuccesful POST of " + path + "!\n";
    }
    
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String put(@PathParam("path") String path) {
        boolean result = false;
        if (result) {
            return "successful PUT of " + path + "!\n";
        }
        return "unsuccesful PUT of " + path + "!\n";
    }
}
