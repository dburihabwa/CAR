package fr.lille1.car.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import fr.lille1.car.rmi.impl.SiteImpl;

public class RegistryLauncher {

	public static void main(String[] args) throws RemoteException {
		Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		registry.rebind("dummy", (Remote) new SiteImpl("dummy"));
	}

}
