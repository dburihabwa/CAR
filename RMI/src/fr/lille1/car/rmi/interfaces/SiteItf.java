package fr.lille1.car.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SiteItf extends Remote {
	List<SiteItf> getChildren() throws RemoteException;

	SiteItf getParent() throws RemoteException;

	String getMessage() throws RemoteException;

	boolean isRoot() throws RemoteException;

	boolean addChild(final SiteItf child) throws RemoteException;

	boolean setParent(final SiteItf parent) throws RemoteException;

	boolean setMessage(final String message) throws RemoteException;

	void propagate() throws RemoteException;

	String getName() throws RemoteException;
}
