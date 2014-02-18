package main.java.car.ftp;

import java.io.IOException;

/**
 * Get an instance of the FTP server, initialize it and start it.
 * 
 * @author dorian
 * 
 */
public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("java FTP.jar");
			System.exit(0);
		}
		int port = Integer.parseInt(args[0]);
		String directoryPath = args[1];
		Server server = Server.getInstance();
		server.init(port, directoryPath);
		server.loadUsers("./users.properties");
		System.out.println("Starting server on port: " + port);
		server.start();
		System.out.println("Stopping server!");
	}
}
