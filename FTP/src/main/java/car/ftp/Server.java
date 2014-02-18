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

public class Server {
	/** Socket receiving client connections */
	private ServerSocket socket;
	/** Port number for the server */
	private int port = -1;
	/** Root directory for the server */
	private File rootDirectory;
	public Properties users = new Properties();

	private static Logger logger = Logger.getAnonymousLogger();

	private static Server INSTANCE = null;

	private Server() {
		;
	}

	public static Server getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Server();
		}
		return INSTANCE;
	}

	public void init(final int port, final String directoryPath) {
		this.rootDirectory = new File(directoryPath);
		this.port = port;
		if (!this.rootDirectory.exists()) {
			throw new IllegalArgumentException("The directory " + rootDirectory + " does not exist!");
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

	public ServerSocket getSocket() {
		if (socket == null) {
			throw new IllegalStateException(
					"The server is not running at the moment!");
		}
		return socket;
	}

	public int getPort() {
		if (port == -1) {
			throw new IllegalStateException(
					"The port has not been initialzed yet!");
		}
		return port;
	}

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
