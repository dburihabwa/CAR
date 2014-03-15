/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.lille1.car.burihabwa.rest.api;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author dorian
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {
    
    public static String host = "localhost";
    public static int port = 1024;
    public static String username = "anonymous";
    public static String password  = "";

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(fr.lille1.car.burihabwa.rest.api.DirResource.class);
        resources.add(fr.lille1.car.burihabwa.rest.api.FileResource.class);
    }
    
}
