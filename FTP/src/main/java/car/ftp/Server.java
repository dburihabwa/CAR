package main.java.car.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.utils.FtpWorker;

public class Server {
	/** Socket receiving client connections */
	private ServerSocket socket;
	/** Port number for the server */
	private int port;
	/** Root directory for the server */
	private File rootDirectory;

	private Logger logger = Logger.getAnonymousLogger();

	public static Server SERVER = null;

	private Server() {
		;
	}

	public static Server getServer(int port, String directoryPath) {
		if (SERVER != null) {
			return SERVER;
		}
		SERVER = new Server();
		SERVER.rootDirectory = new File(directoryPath);
		SERVER.port = port;
		if (!SERVER.rootDirectory.exists()) {
			throw new IllegalArgumentException("The directory does not exist!");
		}
		if (!SERVER.rootDirectory.isDirectory()) {
			throw new IllegalArgumentException(
					"The directory path does not point to a directory!");
		}
		if (!SERVER.rootDirectory.canRead() || !SERVER.rootDirectory.canWrite()
				|| !SERVER.rootDirectory.canExecute()) {
			throw new IllegalArgumentException(
					"The server does run with the rights to read, write and execute on the directory!");
		}
		return SERVER;
	}

	public void start() throws IOException {
		System.out.println("The server is now starting!");
		this.socket = null;
		BufferedInputStream bis = null;
		try {
			socket = new ServerSocket(this.port);
			logger.log(Level.INFO, "The server will now wait for connections on port " + this.port);
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
		return socket;
	}

	public int getPort() {
		return port;
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	public static Server getSERVER() {
		return SERVER;
	}
}
