package main.java.car.ftp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.ClientSession;
import main.java.car.ftp.FtpRequest;
import main.java.car.ftp.exceptions.UnsupportedCommandException;

public class FtpWorker implements Runnable {
	private Socket socket;
	private boolean connected;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public FtpWorker(Socket socket) {
		if (socket == null)
			throw new IllegalArgumentException("socket argument cannot be null");
		this.socket = socket;
		this.connected = true;
	}

	@Override
	public void run() {
		BufferedInputStream bis = null;
		DataOutputStream dos = null;
		try {
			bis = new BufferedInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeBytes("200 Connexion accepted!\n");
			ClientSession clientSession = new ClientSession(socket, true);
			FtpRequest ftpRequest = new FtpRequest(clientSession);
			String command = null;
			while (connected && !socket.isClosed()) {
				System.out.println("Entering LOOP");
				InputStreamReader isr = new InputStreamReader(
						socket.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				do {
					command = br.readLine();
				} while (command == null);
				logger.log(Level.INFO, "Command : " + command);
				try {
					ftpRequest.processRequest(command);
				} catch (UnsupportedCommandException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
					dos.writeBytes("502 The client's request cannot be fulfilled by the server\n");
				}
				logger.log(Level.INFO, "Waiting for new command");
				command = null;
			}
			logger.log(Level.INFO, "The client closed the connection!");
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			try {
				dos.writeBytes("451 An error occured while processing the client's request!\n");
			} catch (IOException e1) {
				logger.log(Level.SEVERE, "Cannot recover from error");
				System.exit(1);
			}
		} finally {

			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					System.exit(1);
				}
			}
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					System.exit(1);
				}
			}
		}

	}

}
