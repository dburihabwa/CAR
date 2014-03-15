/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.api;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
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
        content += "\t<script type = \"text/javascript\" src = \"rest.js\"></script>";
        content += "</head>\n<body>\n";
        content += "<h1>" + "</h1>\n";
        try {
            adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, ApplicationConfig.username, ApplicationConfig.password);
            files = adapter.list(path);
        } catch (IOException ex) {
            Logger.getLogger(DirResource.class.getName()).log(Level.SEVERE, null, ex);
            content += "\t<h1>An error occured</h1>\n";
            content += "\t<p>" + ex.getMessage() + "</p>\n";
            content += "</body>";
            content += "</html>";
            return content;
        } finally {
            adapter.close();
        }
        content += "\t<h1>You asked for " + path + "</h1>\n";
        content += "\t<ul>\n";
        for (FTPFile file : files) {
            content += "\t\t<li>";
            if (file.isDirectory()) {
                content += "<a";
                content += " title = \"" + file.getName() + "\"";
                content += " href = \"";
                if (path != null) {
                    content += context.getAbsolutePath().getPath() + "/";
                }
                content += file.getName() + "\">" + file.getName() + "</a>";
            } else {
                content += file.getName();
            }
            content += "</li>\n";
        }
        content += "\t</ul>\n";
        content += "</body>";
        content += "</html>";
        return content;
    }
}
