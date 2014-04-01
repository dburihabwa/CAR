package fr.lille1.car.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import fr.lille1.car.rmi.interfaces.SiteItf;
import fr.lille1.car.rmi.utils.Prompt;

/**
 * Launches a standalone program enabling a user to send messages to a live
 * {@link SiteItf}.
 * 
 * @author Dorian Burihabwa
 * 
 */
public class Commander {
	public static void main(String[] args) throws RemoteException,
			NotBoundException {
		if (args.length != 3) {
			System.out
					.println("Usage: java -jar commander.jar <registry host> <registry port> <node name>");
			System.exit(0);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String name = args[2];
		Registry registry = LocateRegistry.getRegistry(host, port);
		SiteItf site = (SiteItf) registry.lookup(name);
		Prompt prompt = new Prompt(site);
		prompt.setSender(site);
		System.out.print("> ");
		while (prompt.nextLine() != null) {
			System.out.print("> ");
		}
	}
}
