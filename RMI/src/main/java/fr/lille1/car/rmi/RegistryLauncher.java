package fr.lille1.car.rmi;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import fr.lille1.car.rmi.impl.SiteImpl;
import fr.lille1.car.rmi.interfaces.SiteItf;

/**
 * Launches the RMI {@link Registry} where {@link SiteItf} can be bound and
 * looked up at runtime.
 * 
 * @author Dorian Burihabwa
 * 
 */
public class RegistryLauncher {

	/**
	 * Starts the RMIRegistry.
	 * 
	 * @param args
	 *            unused
	 * @throws RemoteException
	 *             If an error occurs while instantiating or running the
	 *             {@link Registry}.
	 * @throws NotBoundException
	 *             If an error occurs while unbounding an unbound object from
	 *             the {@link Registry}.
	 */
	public static void main(String[] args) throws RemoteException,
			NotBoundException {
		Registry registry = LocateRegistry
				.createRegistry(Registry.REGISTRY_PORT);
		registry.rebind("dummy", (Remote) new SiteImpl("dummy"));
	}

}
