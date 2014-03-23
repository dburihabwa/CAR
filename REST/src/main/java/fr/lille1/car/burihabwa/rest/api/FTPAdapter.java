/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author dorian
 */
public interface FTPAdapter {
    FTPFile[] list(final String path) throws IOException;
    boolean exists(final String path) throws IOException;
    boolean isDirectory(final String path) throws IOException;
    File retr(final String path) throws IOException;
    void stor(final String path, final InputStream is) throws IOException;
    boolean delete(final String path) throws IOException;
}
