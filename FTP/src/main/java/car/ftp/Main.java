package main.java.car.ftp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Get an instance of the FTP server, initialize it and start it.
 * 
 * @author dorian
 * 
 */
public class Main {
	private static Logger logger = Logger.getAnonymousLogger();
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out
					.println("Usage: java -jar FTP.jar <port> <ftp root directory>");
			System.exit(0);
		}
		int port = Integer.parseInt(args[0]);
		String directoryPath = args[1];
		Server server = Server.getInstance();
		server.init(port, directoryPath);
		try {
		server.loadUsers("./users.properties");
		} catch (IOException e) {
			logger.log(Level.WARNING, "The users file could not be read!");
		}
		System.out.println("Starting server on port: " + port);
		server.start();
		System.out.println("Stopping server!");
	}
}
