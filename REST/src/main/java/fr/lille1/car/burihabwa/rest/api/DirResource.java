/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.api;

import fr.lille1.car.burihabwa.rest.utils.BasicAuthenticator;
import fr.lille1.car.burihabwa.rest.utils.FTPAdapter;
import fr.lille1.car.burihabwa.rest.utils.FTPAdapterImpl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.net.ftp.FTPFile;

/**
 * RESTFUL Web service that allows interacting with directories on a FTP server.
 *
 * @author dorian
 */
@Path("")
public class DirResource {

    @Context
    private UriInfo context;

    @Context
    private HttpHeaders headers;

    @GET
    @Path("/dir")
    @Produces("text/html")
    /**
     * Liste the files at the root of the server
     *
     * @throws IOException
     */
    public Response list() throws IOException {
        return list(null);
    }

    /**
     * Lists the content of a directory on the server as HTML.
     *
     * @param path Path to the directory on the server
     *
     * @return the content of the directory as HTML
     * @throws IOException If an error occurs while interacting with the server
     */
    @GET
    @Produces("text/html")
    @Path("/dir/{path:.*}")
    public Response list(@PathParam("path") String path) throws IOException {
        if (headers.getRequestHeader("authorization") == null) {
            Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Requires HTTP authentication!");
            return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
        }
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(headers.getRequestHeader("authorization").get(0));
        String username = basicAuthenticator.getUsername();
        String password = basicAuthenticator.getPassword();
        System.out.println("Try to authenticate as " + username + " with password \"" + password + "\"");
        FTPAdapter adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, username, password);
        FTPFile[] files = null;
        String content = "<!DOCTYPE html>\n";
        content += "<html>\n";
        content += "<head>\n";
        content += "\t<title>/dir/</title>\n";
        content += "</head>\n<body>\n";
        content += "<h1>" + "</h1>\n";
        try {
            if (!adapter.hasValidCredentials()) {
                Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Wrong user name and password!");
                return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
            }
            files = adapter.list(path);
        } catch (IOException ex) {
            Logger.getLogger(DirResource.class.getName()).log(Level.SEVERE, null, ex);
            content += "\t<h1>An error occured</h1>\n";
            content += "\t<p>" + ex.getMessage() + "</p>\n";
            content += "</body>\n";
            content += "</html>\n";
            Response.ResponseBuilder response = Response.ok(content, MediaType.TEXT_HTML);
            return response.build();
        } finally {
            adapter.close();
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
        content += "\t\t\t<tr>\n";
        content += "\t\t\t\t<td><a href =\"" + path + "\">.</a></td>\n";
        content += "\t\t\t\t<td></td>\n";
        content += "\t\t\t</tr>\n";
        content += "\t\t\t<tr>\n";
        content += "\t\t\t\t<td><a href =\"" + adapter.getParentDirectory(path) + "\">..</a></td>\n";
        content += "\t\t\t\t<td></td>\n";
        content += "\t\t\t</tr>\n";
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
        Response.ResponseBuilder response = Response.ok(content, MediaType.TEXT_HTML).status(Response.Status.OK);
        return response.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/dir/{path:.*}")
    /**
     * Lists the content of a directory on the server as JSON.
     *
     * @param path Path to the directory on the server
     *
     * @return the content of the directory as JSON
     * @throws IOException If an error occurs while interacting with the server
     */
    public Response listJSON(@PathParam("path") String path) throws IOException {
        if (headers.getRequestHeader("authorization") == null) {
            Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Requires HTTP authentication!");
            return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
        }
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(headers.getRequestHeader("authorization").get(0));
        String username = basicAuthenticator.getUsername();
        String password = basicAuthenticator.getPassword();
        FTPAdapter adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, username, password);
        FTPFile[] files = null;
        String content = "[";
        try {
            if (!adapter.hasValidCredentials()) {
                Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Wrong user name and password!");
                return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
            }
            files = adapter.list(path);
        } catch (IOException ex) {
            Logger.getLogger(DirResource.class.getName()).log(Level.SEVERE, null, ex);
            content += "An error occured]";
            Response.ResponseBuilder response = Response.status(Response.Status.NOT_FOUND)
                    .entity(content);
            return response.build();
        } finally {
            adapter.close();
        }
        for (FTPFile file : files) {
            content += "{ ";
            content += "name: " + file.getName() + ", ";
            content += "size: " + file.getSize() + ", ";
            content += "type: " + (file.isDirectory() ? "directory" : "file");
            content += " }, ";
        }
        content += "]";
        Response.ResponseBuilder response = Response.status(Response.Status.OK)
                .entity(content);
        return response.build();
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/dir/{path:.*}")
    /**
     * Deletes an empty directory on the server.
     *
     * @param path Path to the directory on the server
     *
     * @return the response with the result of the deletion
     * @throws IOException If an error occurs while interacting with the server
     */
    public Response delete(@PathParam("path") String path) throws IOException {
        Response.ResponseBuilder response = null;
        if (headers.getRequestHeader("authorization") == null) {
            response = Response.status(Response.Status.UNAUTHORIZED).entity("Requires HTTP authentication!");
            return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
        }
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(headers.getRequestHeader("authorization").get(0));
        String username = basicAuthenticator.getUsername();
        String password = basicAuthenticator.getPassword();

        FTPAdapter adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, username, password);
        boolean result = false;
        try {
            if (!adapter.hasValidCredentials()) {
                response = Response.status(Response.Status.UNAUTHORIZED).entity("Wrong user name and password!");
                return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
            }

            result = adapter.rmdir(path);
        } catch (IOException ex) {
            Logger.getLogger(DirResource.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            adapter.close();
        }
        if (result) {
            response = Response.status(Response.Status.OK).entity("successful DELETE of " + path + "!\n");
            return response.build();
        }
        response = Response.status(Response.Status.NOT_FOUND).entity("unsuccesful DELETE of " + path + "!\n");
        return response.build();
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/dir/{path:.*}")
    /**
     * Creates an empty directory on the server.
     *
     * @param path Path to the new directory on the server
     *
     * @return the response with the result of the creation
     * @throws IOException If an error occurs while interacting with the server
     */
    public Response put(@PathParam("path") String path) throws IOException {
        Response.ResponseBuilder response = null;
        if (headers.getRequestHeader("authorization") == null) {
            response = Response.status(Response.Status.UNAUTHORIZED).entity("Requires HTTP authentication!");
            return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
        }
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(headers.getRequestHeader("authorization").get(0));
        String username = basicAuthenticator.getUsername();
        String password = basicAuthenticator.getPassword();

        String message = "PUT /api/dir/" + path;
        Logger.getLogger(FileResource.class.getName()).log(Level.INFO, message);
        FTPAdapter adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, username, password);
        try {
            if (!adapter.hasValidCredentials()) {
                response = Response.status(Response.Status.UNAUTHORIZED).entity("Wrong user name and password!");
                return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
            }
            boolean result = adapter.mkdir(path);
            if (result) {
                message += ": SUCCESS";
                response = Response.status(Response.Status.OK).entity(message);
            } else {
                message += ": FAILURE";
                response = Response.status(Response.Status.NOT_FOUND).entity(message);
            }
        } catch (IOException e) {
            Logger.getLogger(FileResource.class.getName()).log(Level.WARNING, e.getMessage());
            message += e.getMessage();
            response = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage());
            return response.build();
        } finally {
            adapter.close();
        }
        return response.build();
    }
}
