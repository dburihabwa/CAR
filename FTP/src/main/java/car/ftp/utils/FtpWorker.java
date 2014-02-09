package main.java.car.ftp.utils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.car.ftp.FtpRequest;

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
			dos.writeBytes("200 Connexion accéptée!\n");
			FtpRequest ftpRequest = new FtpRequest(socket);
			byte[] buffer = new byte[1024];
			String command;
			while (connected && !socket.isClosed()) {
				System.out.println("Entering LOOP");
				bis = new BufferedInputStream(socket.getInputStream());
				while (bis.available() == 0)
					;
				bis.read(buffer);
				command = new String(buffer);
				ftpRequest.processRequest(command);
				System.out.println("LOOPING");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			System.exit(1);
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
