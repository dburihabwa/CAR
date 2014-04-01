package fr.lille1.car.rmi.utils;

import java.rmi.RemoteException;
import java.util.Scanner;

import fr.lille1.car.rmi.impl.MessageImpl;
import fr.lille1.car.rmi.interfaces.SiteItf;

public class Prompt {
	private Scanner scanner;
	private SiteItf sender;
	private SiteItf destination;

	public Prompt(final SiteItf dest) {
		if (dest == null) {
			throw new IllegalArgumentException("dest argument cannot be null!");
		}
		this.destination = dest;
		scanner = new Scanner(System.in);
	}

	public void setSender(final SiteItf sender) {
		this.sender = sender;
	}

	public String nextLine() throws RemoteException {
		String content = scanner.nextLine();
		if (content == null) {
			return null;
		}
		destination.receive(new MessageImpl(this.sender, content));
		return content;
	}

}
