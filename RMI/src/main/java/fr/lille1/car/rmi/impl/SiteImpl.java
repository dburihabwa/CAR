package fr.lille1.car.rmi.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import fr.lille1.car.rmi.interfaces.SiteItf;

public class SiteImpl extends UnicastRemoteObject implements SiteItf {
	private static final long serialVersionUID = 2885566260151215904L;
	private List<SiteItf> children;
	private SiteItf parent;
	private String message;
	private String name;

	@SuppressWarnings("unused")
	private SiteImpl() throws RemoteException {

	}

	public SiteImpl(final String name) throws RemoteException {
		this.name = name;
		this.children = new ArrayList<SiteItf>();
	}

	public List<SiteItf> getChildren() throws RemoteException {
		return this.children;
	}

	public SiteItf getParent() throws RemoteException {
		return this.parent;
	}

	public boolean addChild(SiteItf child) throws RemoteException {
		if (child == null) {
			throw new IllegalArgumentException("child argument cannot be null!");
		}
		return this.children.add(child);
	}

	public boolean setParent(SiteItf parent) throws RemoteException {
		if (parent == null) {
			throw new IllegalArgumentException(
					"parent argument cannot be null!");
		}
		this.parent = parent;
		return parent == this.parent;
	}

	public void propagate() throws RemoteException {
		System.out.println("Will now send the message to " + children.size());
		for (SiteItf child : this.children) {
			child.setMessage(message);
		}
		this.message = null;
	}

	public boolean setMessage(final String message) throws RemoteException {
		if (message == null) {
			throw new IllegalArgumentException(
					"message argument cannot be null");
		}
		System.out.println("RECEIVED : " + message);
		this.message = message;
		return this.message == message;
	}

	public String getMessage() throws RemoteException {
		return this.message;
	}

	public boolean isRoot() throws RemoteException {
		return this.parent == null;
	}

	public String getName() throws RemoteException {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SiteImpl other = (SiteImpl) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}

}
