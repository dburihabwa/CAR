/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.lille1.car.burihabwa.rest.utils;

import java.io.IOException;
import sun.misc.BASE64Decoder;

/**
 *
 * @author dorian
 */
public class BasicAuthenticator {
    private String base64String;
    
    public BasicAuthenticator(final String base64String) {
        this.base64String = base64String;      
    }
    public String decode() throws IOException {
        return new String(new BASE64Decoder().decodeBuffer(this.base64String.split("\\s+")[1]));
    }
    
    public String getUsername() throws IOException {
        return decode().split(":")[0];
    }
    
    public String getPassword() throws IOException {
        return decode().split(":")[1];
    }
}
