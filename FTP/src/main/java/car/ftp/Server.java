package main.java.car.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.utils.FtpWorker;

/**
 * Server that listens for incoming connections and dispatches the new clients
 * to a specific thread that will handle their requests.
 * 
 * @author dorian
 * 
 */
public class Server {
	/** Socket receiving client connections */
	private ServerSocket socket;
	/** Port number for the server */
	private int port = -1;
	/** Root directory for the server */
	private File rootDirectory;
	/** List of authorized users and their passwords */
	public Properties users = new Properties();

	private static Logger logger = Logger.getAnonymousLogger();

	/**
	 * Singleton {@link Server} instance.
	 */
	private static Server INSTANCE = null;

	private Server() {
		;
	}

	/**
	 * Returns the singleton instance of server.
	 * 
	 * @return the singleton instance of server.
	 */
	public static Server getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Server();
		}
		return INSTANCE;
	}

	/**
	 * Initialize the basici parameters for the server to run.
	 * 
	 * @param port
	 *            The port the server should running on
	 * @param directoryPath
	 *            Path to the root directory of the server
	 */
	public void init(final int port, final String directoryPath) {
		this.rootDirectory = new File(directoryPath);
		this.port = port;
		if (!this.rootDirectory.exists()) {
			throw new IllegalArgumentException("The directory " + rootDirectory
					+ " does not exist!");
		}
		if (!this.rootDirectory.isDirectory()) {
			throw new IllegalArgumentException(
					"The directory path does not point to a directory!");
		}
		if (!this.rootDirectory.canRead() || !this.rootDirectory.canWrite()
				|| !this.rootDirectory.canExecute()) {
			throw new IllegalArgumentException(
					"The server does run with the rights to read, write and execute on the directory!");
		}
	}

	/**
	 * Starts the server.
	 * 
	 * @throws IOException
	 *             In case an error occurs while trying to on the server's
	 *             socket.
	 */
	public void start() throws IOException {
		if (this.port == -1 || this.rootDirectory == null) {
			throw new IllegalStateException(
					"The server has not been initialized yet!");
		}
		this.socket = null;
		BufferedInputStream bis = null;
		try {
			socket = new ServerSocket(this.port);
			logger.log(Level.INFO,
					"The server will now wait for connections on port "
							+ this.port);
			while (true) {
				Socket clientSocket = socket.accept();
				FtpWorker worker = new FtpWorker(clientSocket);
				Thread t = new Thread(worker);
				t.start();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString());
			System.exit(2);
		} finally {
			if (bis != null)
				bis.close();
			if (!socket.isClosed())
				socket.close();
		}
	}

	/**
	 * Returns the server Socket that accepts connections.
	 * 
	 * @return The server Socket that accepts connections.
	 */
	public ServerSocket getSocket() {
		if (socket == null) {
			throw new IllegalStateException(
					"The server is not running at the moment!");
		}
		return socket;
	}

	/**
	 * Returns the port number that on which the server listening for new
	 * connections.
	 * 
	 * @return Port number that on which the server listening for new
	 *         connections.
	 */
	public int getPort() {
		if (port == -1) {
			throw new IllegalStateException(
					"The port has not been initialzed yet!");
		}
		return port;
	}

	/**
	 * Returns the location of the root directory.
	 * 
	 * @return The server's root directory
	 */
	public File getRootDirectory() {
		if (rootDirectory == null) {
			throw new IllegalStateException(
					"The root directory has not been initialzed yet!");
		}
		return rootDirectory;
	}

	/**
	 * Returns a map containing the authorized users and their password.
	 * 
	 * @return the authorized users and their password.
	 */
	public Properties getUsers() {
		return this.users;
	}

	/**
	 * Loads the list of users and their password from a file.
	 * 
	 * @param path
	 *            Path to the user file
	 */
	public void loadUsers(final String path) {
		if (path == null) {
			throw new IllegalArgumentException("argument path cannot be null!");
		}
		try {
			FileInputStream fis = new FileInputStream(path);
			this.users.load(fis);
			fis.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}
}
