package fr.lille1.car.burihabwa.rest.api;

import com.sun.jersey.multipart.FormDataParam;
import fr.lille1.car.burihabwa.rest.utils.BasicAuthenticator;
import fr.lille1.car.burihabwa.rest.utils.FTPAdapter;
import fr.lille1.car.burihabwa.rest.utils.FTPAdapterImpl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * RESTFUL Web Service allowing interaction with files on a FTP server.
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
    public Response get(@PathParam("path") String path) throws IOException {
        if (headers.getRequestHeader("authorization") == null) {
            Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Requires HTTP authentication!");
            return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
        }
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(headers.getRequestHeader("authorization").get(0));
        String username = basicAuthenticator.getUsername();
        String password = basicAuthenticator.getPassword();
        FTPAdapter adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, username, password);
        File file = null;
        try {
            if (!adapter.hasValidCredentials()) {
                Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Wrong user name and password!");
                return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
            }
            file = adapter.retr(path);
        } catch (IOException e) {

        } finally {
            adapter.close();
        }
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachement; filename = " + file.getName());
        return response.build();
    }

    @POST
    @Path("/file/{path:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response post(@PathParam("path") String path, @FormDataParam("file") InputStream received) throws IOException {
        if (headers.getRequestHeader("authorization") == null) {
            Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Requires HTTP authentication!");
            return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
        }
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(headers.getRequestHeader("authorization").get(0));
        String username = basicAuthenticator.getUsername();
        String password = basicAuthenticator.getPassword();
        FTPAdapterImpl adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, username, password);
        try {
            if (!adapter.hasValidCredentials()) {
                Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Wrong user name and password!");
                return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
            }
            if (path == null) {
            }
            if (!adapter.exists(path)) {
                ResponseBuilder response = Response.status(Response.Status.NOT_FOUND);
                return response.entity("failure: file does not exist").build();
            }
            if (adapter.isDirectory(path)) {
                ResponseBuilder response = Response.status(Response.Status.NOT_MODIFIED);
                return response.entity("failure: cannot modify directory").build();
            }
            adapter.stor(path, received);
        } catch (IOException ex) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, null, ex);
            ResponseBuilder response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.entity("failure : " + ex.getMessage()).build();
        } finally {
            adapter.close();
        }
        ResponseBuilder response = Response.status(Response.Status.OK);
        return response.entity("success").build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_OCTET_STREAM})
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/file/{path:.*}")
    /**
     * Creates or overwrites a file on the server.
     *
     * @param path Path of the file
     * @param received file input stream
     */
    public Response put(@PathParam("path") String path, InputStream received) throws IOException {
        if (path == null || path.isEmpty()) {
            ResponseBuilder response = Response.status(400); //Bad request
            return response.entity("failure: the new file must be given a name!").build();
        }
        if (headers.getRequestHeader("authorization") == null) {
            Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Requires HTTP authentication!");
            return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
        }
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(headers.getRequestHeader("authorization").get(0));
        String username = basicAuthenticator.getUsername();
        String password = basicAuthenticator.getPassword();
        FTPAdapterImpl adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, username, password);

        try {
            if (!adapter.hasValidCredentials()) {
                Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Wrong user name and password!");
                return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
            }

            adapter.stor(path, received);
        } catch (IOException ex) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, null, ex);
            ResponseBuilder response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.entity("failure: " + ex.getMessage()).build();
        } finally {
            adapter.close();
        }

        ResponseBuilder response = Response.status(Response.Status.OK);
        return response.entity("success").build();
    }

    @DELETE
    @Path("/file/{path: .*}")
    /**
     * Deletes a file on the server.
     *
     * @param path Path to the file
     * @return A string as the result of the operation
     */
    public Response delete(@PathParam("path") final String path) throws IOException {
        if (headers.getRequestHeader("authorization") == null) {
            Response.ResponseBuilder response = Response.status(Response.Status.UNAUTHORIZED).entity("Requires HTTP authentication!");
            return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
        }
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(headers.getRequestHeader("authorization").get(0));
        String username = basicAuthenticator.getUsername();
        String password = basicAuthenticator.getPassword();
        FTPAdapterImpl adapter = new FTPAdapterImpl(ApplicationConfig.host, ApplicationConfig.port, username, password);
        Response.ResponseBuilder response = null;
        try {
            if (!adapter.hasValidCredentials()) {
                response = Response.status(Response.Status.UNAUTHORIZED).entity("Wrong user name and password!");
                return response.header("Www-authenticate", "Basic realm=\"rest\"").build();
            }
            boolean result = adapter.delete(path);
            if (result) {
                response = Response.status(Response.Status.OK).entity("success");
            } else {
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("failure");
            }
        } catch (IOException e) {
            Logger.getLogger(FileResource.class.getName()).log(Level.WARNING, e.getMessage());
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
            return response.build();
        } finally {
            adapter.close();
        }
        return response.build();
    }
}
