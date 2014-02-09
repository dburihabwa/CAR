package main.java.car.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.utils.FtpWorker;

public class Main {
	private static Logger logger = Logger.getAnonymousLogger();
	public static int port;
	public static File directory;

	public static void initialize(int portNumber, String directoryPath) {
		if (portNumber < 1024) {
			throw new IllegalArgumentException(
					"This server should not run on a port lower than 1024!");
		}
		if (directoryPath == null) {
			throw new IllegalArgumentException(
					"The directory argument cannot be null!");
		}
		port = portNumber;
		directory = new File(directoryPath);
		if (!directory.exists()) {
			throw new IllegalArgumentException("The directory " + directoryPath
					+ " does not exist!");
		}
		if (!directory.canRead() || !directory.canExecute()) {
			throw new IllegalArgumentException(
					"The content of the server directory cannot be read!");
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("java FTP.jar");
			System.exit(0);
		}
		initialize(Integer.parseInt(args[0]), args[1]);
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
