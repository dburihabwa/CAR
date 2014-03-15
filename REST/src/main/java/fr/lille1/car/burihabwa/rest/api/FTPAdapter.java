/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.api;

import java.io.File;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author dorian
 */
public interface FTPAdapter {
    FTPFile[] list(final String path) throws IOException;
    File retr(final String path) throws IOException;
    void stor(final File file) throws IOException;
}
