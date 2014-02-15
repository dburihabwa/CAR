package main.java.car.ftp;

import java.io.IOException;

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
		System.out.println("Starting server on port: " + port);
		server.start();
		System.out.println("Stopping server!");
	}
}
