package main.java.car.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.utils.FtpWorker;

public class Server {
	/** Socket receiving client connections */
	private ServerSocket socket;
	/** Port number for the server */
	private int port;
	/** Number of workers available to the server */
	private AtomicInteger workers;
	/** Workers at server's disposal */
	private List<FtpWorker> ftpWorkers;
	/** Root directory for the server */
	private File rootDirectory;

	private Logger logger = Logger.getAnonymousLogger();

	private Server() {
		;
	}

	public Server(int port, int workers, String directoryPath) {
		this.workers = new AtomicInteger(workers);
		this.ftpWorkers = new ArrayList<FtpWorker>(workers);
		this.rootDirectory = new File(directoryPath);
		if (!rootDirectory.exists()) {
			throw new IllegalArgumentException("The directory does not exist!");
		}
		if (!rootDirectory.isDirectory()) {
			throw new IllegalArgumentException(
					"The directory path does not point to a directory!");
		}
		if (!rootDirectory.canRead() || !rootDirectory.canWrite()
				|| !rootDirectory.canExecute()) {
			throw new IllegalArgumentException(
					"The server does run with the rights to read, write and execute on the directory!");
		}
	}

	public void start() throws IOException {
		ServerSocket socket = null;
		BufferedInputStream bis = null;
		try {
			socket = new ServerSocket(1024);
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
}
