/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author dorian
 */
public class FTPAdapterImpl implements FTPAdapter {

    private FTPClient client;
    private String username;
    private String password;
    private String host;
    private int port;

    public FTPAdapterImpl(final String host, final int port, final String username, final String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    private boolean authenticate() throws IOException {
        this.client = new FTPClient();
        this.client.connect(host, port);
        this.client.login(username, password);
        return this.client.isConnected();
    }

    @Override
    public FTPFile[] list(final String path) throws IOException {
        if (!authenticate()) {
            throw new IOException("Wrong username and password!");
        }
        if (path != null && !path.isEmpty()) {
            if (!this.client.changeWorkingDirectory(path)) {
                throw new IOException("Could not change directory!");
            }
        }
        FTPFile[] files = client.listFiles();
        return files;
    }

    public boolean close() throws IOException {
        return this.client.logout();
    }

    /**
     *
     * @param path
     * @return
     * @throws IOException
     */
    @Override
    public File retr(final String path) throws IOException {
        if (!authenticate()) {
            throw new IOException("Wrong username and password!");
        }
        if (path == null || path.isEmpty()) {
            throw new IOException("A path to the file must be given!");
        }
        File file = File.createTempFile("out", "tmp");
        file.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(file);
        this.client.setBufferSize(4096);
        this.client.setFileType(FTP.BINARY_FILE_TYPE);
        boolean retr = this.client.retrieveFile(path, fos);
        fos.close();
        this.client.logout();
        this.client.disconnect();
        return file;
    }

    @Override
    public void stor(final File file) throws IOException {
        if (!authenticate()) {
            throw new IOException("Wrong username and password!");
        }
        if (file == null) {
            throw new IllegalArgumentException("File argument cannot be null!");
        }
        InputStream fis = new FileInputStream(file);
        this.client.storeFile(file.getName(), fis);
        this.client.logout();
        this.client.disconnect();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
