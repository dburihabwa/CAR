/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.utils;

import java.io.IOException;
import java.util.Base64;

/**
 *
 * Allows manipulation of Basic authentication tokens passed in the
 * authorization header.
 *
 * @author dorian
 */
public class BasicAuthenticator {

    private String base64String;

    public BasicAuthenticator(final String base64String) {
        this.base64String = base64String;
    }

    /**
     * Decodes the base 64 part of the header
     *
     * @return The decoded base 64 of the authorization header
     *
     * @throws IOException If an error occurs while decoding the header
     */
    public String decode() throws IOException {
        return new String(Base64.getDecoder().decode(this.base64String.split("\\s+")[1]));
    }

    /**
     * Returns the username in the header
     *
     * @return The username in the header
     * @throws IOException If an error occurs while decoding the field
     */
    public String getUsername() throws IOException {
        return decode().split(":")[0];
    }

    /**
     * Returns the password in the header
     *
     * @return Password in the header
     * @throws IOException If an error occurs while decoding the field
     */
    public String getPassword() throws IOException {
        return decode().split(":")[1];
    }
}
