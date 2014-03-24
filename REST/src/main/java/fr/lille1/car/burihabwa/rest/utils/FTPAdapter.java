package fr.lille1.car.burihabwa.rest.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Abstraction layer in front of the FTP server.
 *
 * @author dorian
 */
public interface FTPAdapter {

    /**
     * Lists the file in the directory on the server.
     *
     * @param path Path to the directory
     * @return A list of files in the directory
     * @throws IOException If the directory could not be found or its content
     * read
     */
    FTPFile[] list(final String path) throws IOException;

    /**
     * Checks if a file exists on the server
     *
     * @param path Path to the file on the server
     * @return Result of the test
     * @throws IOException
     */
    boolean exists(final String path) throws IOException;

    /**
     * Checks if the file on the server is a directory
     *
     * @param path Path to the file
     * @return Result of the test
     * @throws IOException If the file could not be found
     */
    boolean isDirectory(final String path) throws IOException;

    /**
     * Retrieves a file from the server.
     *
     * @param path Path to the file on the server
     * @return A copy of the file
     * @throws IOException If the file could not found or read.
     */
    File retr(final String path) throws IOException;

    /**
     * Sends a file to the server
     *
     * @param path Path to the new file on the server
     * @param is Content of the file
     * @throws IOException If the file could not be created properly due to a
     * erroneous path or lack of rights.
     */
    void stor(final String path, final InputStream is) throws IOException;

    /**
     * Deletes a file from the server
     *
     * @param path Path to the file
     * @return Result of the deletion
     * @throws IOException If the file could not be found or deleted
     */
    boolean delete(final String path) throws IOException;

    /**
     * Creates a directory.
     *
     * @param path Path to the new directory
     * @return Result of the creation
     * @throws IOException If the directory cannot be created or one of the
     * directories in the subpath could not be found.
     */
    boolean mkdir(final String path) throws IOException;

    /**
     * Deletes a directory.
     *
     * @param path Path to the directory
     * @return Result of the deletion
     * @throws IOException If the directory could not be found or could not be
     * deleted
     */
    boolean rmdir(final String path) throws IOException;

    /**
     * Tests if the credentials given to the adapter can connect to the server
     *
     * @return Result of the test
     * @throws IOException
     */
    boolean hasValidCredentials() throws IOException;

    /**
     * Closes the connection.
     *
     * @throws IOException
     */
    void close() throws IOException;

    /**
     * Returns the parent directory in the file path.
     *
     * @param path Path to the file
     * @return the parent directory in the file path
     */
    String getParentDirectory(final String path);

    /**
     * Returns the last element of in a path.
     *
     * @param path Path to the file
     * @return The last part of the path
     */
    String getFile(final String path);
}
