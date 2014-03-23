/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.burihabwa.rest.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        this.client = new FTPClient();
    }

    private boolean authenticate() throws IOException {
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
            if (!isDirectory(path)) {
                throw new IOException("Directory does not exist");
            }
            System.out.println("Trying to move to " + path);
            this.client.changeWorkingDirectory(path);
            System.out.println("Currently in " + this.client.printWorkingDirectory());
        }
        FTPFile[] files = client.listFiles();
        return files;
    }

    @Override
    public boolean exists(final String path) throws IOException {
        if (!this.client.isConnected()) {
            this.authenticate();
        }
        String parentDirectory = getParentDirectory(path);
        String file = getFile(path);
        System.out.println("Path :\t" + path);
        System.out.println("\tfolder:\t" + parentDirectory);
        System.out.println("\tfile:\t" + file);
        if (!this.client.changeWorkingDirectory(parentDirectory)) {
            System.out.println("Could not switch to parent directory!");
            return false;
        }

        FTPFile[] files = this.client.listFiles();
        for (FTPFile f : files) {
            System.out.println("FILE :\t" + f);
            if (f.isFile() && f.getName().equalsIgnoreCase(file)) {
                System.out.println("found the file");
                this.client.changeWorkingDirectory("/");
                return true;
            }
        }
        this.client.changeWorkingDirectory("/");
        return false;
    }

    @Override
    public boolean isDirectory(final String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path argument cannot be null!");
        }
        if (!this.client.isConnected()) {
            this.authenticate();
        }
        Path filePath = Paths.get(path);
        int nameCount = filePath.getNameCount();
        System.out.println("NAMECOUNT : " + nameCount);
        FTPFile[] directories = this.client.listDirectories();
        for (FTPFile directory : directories) {
            System.out.println("DIRECTORY :\t" + directory);
            if (directory.getName().equalsIgnoreCase(path)) {
                return true;
            }
        }
        return false;
    }

    public void close() throws IOException {
        if (this.client != null && this.client.isConnected()) {
            this.client.logout();
            this.client.disconnect();
        }
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
        String[] tokens = path.split("/");
        String filename = tokens[tokens.length - 1];
        File file = new File(filename);
        file.createNewFile();
        file.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(file);
        this.client.setBufferSize(4096);
        this.client.setFileType(FTP.BINARY_FILE_TYPE);
        boolean retr = this.client.retrieveFile(path, fos);
        if (!retr) {
            fos.close();
            file.delete();
        }
        fos.close();
        this.client.logout();
        this.client.disconnect();
        return file;
    }

    @Override
    /**
     * Deletes a file on the server.
     *
     * @param path Path to the file
     * @return result of the deletion
     * @throws IOException if the file does not exist, is not a file or the ftp
     * user cannot perform this action.
     */
    public boolean delete(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path argument cannot be null or empty!");
        }
        if (!authenticate()) {
            throw new IOException("Wrong username and password!");
        }
        String currentDirectory = this.client.printWorkingDirectory();
        String parentDirectory = getParentDirectory(path);
        String file = getFile(path);
        if (!parentDirectory.isEmpty()) {
            this.client.changeWorkingDirectory(parentDirectory);
        }
        boolean result = this.client.deleteFile(file);
        String replyString = this.client.getReplyString();
        if (result) {
            Logger.getLogger(FTPAdapterImpl.class.getName()).log(Level.INFO, path + " was successfully deleted!");
        } else {
            Logger.getLogger(FTPAdapterImpl.class.getName()).log(Level.WARNING, replyString);
            close();
            throw new IOException(replyString);
        }
        if (!parentDirectory.isEmpty()) {
            this.client.changeWorkingDirectory(currentDirectory);
        }
        return result;
    }

    public void stor(String path, InputStream received) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path argument cannot be null!");
        }
        if (received == null) {
            throw new IllegalArgumentException("received argument cannot be null!");
        }
        if (!this.client.isConnected()) {
            authenticate();
        }
        String parentDirectory = getParentDirectory(path);
        String file = getFile(path);
        this.client.changeWorkingDirectory(parentDirectory);
        this.client.setBufferSize(4096);
        this.client.setFileType(FTP.BINARY_FILE_TYPE);
        this.client.setFileTransferMode(FTP.COMPRESSED_TRANSFER_MODE);
        boolean storeFile = this.client.storeFile(file, received);
        if (storeFile) {
            System.out.println("The new file " + file + " was created!");
        } else {
            Logger.getLogger(FTPAdapterImpl.class.getName()).log(Level.WARNING, this.client.getReplyString());
            throw new IOException(this.client.getReplyString());
        }
        received.close();
        close();
    }

    public String getParentDirectory(final String path) {
        Path file = Paths.get(path);
        int nameCount = file.getNameCount();
        String parentDirectory = ".";
        if (nameCount > 1) {
            parentDirectory = file.subpath(0, nameCount - 1).toString();
        }
        return parentDirectory;
    }

    public String getFile(final String path) {
        Path completePath = Paths.get(path);
        int nameCount = completePath.getNameCount();
        return completePath.getName(nameCount - 1).toString();
    }

    @Override
    public boolean mkdir(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path argument cannot be null or empty!");
        }
        if (!this.client.isConnected()) {
            authenticate();
        }
        boolean result = this.client.makeDirectory(path);
        if (result) {
            Logger.getLogger(FTPAdapterImpl.class.getName()).log(Level.INFO, this.client.getReplyString());
        } else {
            Logger.getLogger(FTPAdapterImpl.class.getName()).log(Level.WARNING, this.client.getReplyString());
            throw new IOException(this.client.getReplyString());
        }
        return true;
    }

    @Override
    public boolean rmdir(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path argument cannot be null or empty!");
        }
        if (!this.client.isConnected()) {
            authenticate();
        }
        boolean result = this.client.removeDirectory(path);
        if (result) {
            Logger.getLogger(FTPAdapterImpl.class.getName()).log(Level.INFO, this.client.getReplyString());
        } else {
            Logger.getLogger(FTPAdapterImpl.class.getName()).log(Level.WARNING, this.client.getReplyString());
            throw new IOException(this.client.getReplyString());
        }
        return true;
    }

}