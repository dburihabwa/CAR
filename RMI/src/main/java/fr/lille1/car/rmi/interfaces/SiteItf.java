package fr.lille1.car.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface SiteItf extends Remote {
	Set<SiteItf> getChildren() throws RemoteException;

	boolean hasUnsentMessages() throws RemoteException;

	boolean addChild(final SiteItf child) throws RemoteException;

	boolean receive(final Message message) throws RemoteException;

	void propagate() throws RemoteException;

	String getName() throws RemoteException;
}
