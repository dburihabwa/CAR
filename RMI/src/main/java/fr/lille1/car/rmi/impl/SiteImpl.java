package fr.lille1.car.rmi.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.lille1.car.rmi.interfaces.Message;
import fr.lille1.car.rmi.interfaces.SiteItf;

public class SiteImpl extends UnicastRemoteObject implements SiteItf {
	private static final long serialVersionUID = 2885566260151215904L;
	private Set<SiteItf> children;
	private Set<Message> sent;
	private Set<Message> received;
	private String name;
	private Logger logger;

	@SuppressWarnings("unused")
	private SiteImpl() throws RemoteException {

	}

	public SiteImpl(final String name) throws RemoteException {
		if (name == null) {
			throw new IllegalArgumentException("name argument cannot be null!");
		}
		this.name = name;
		this.children = new HashSet<SiteItf>();
		this.received = new HashSet<Message>();
		this.sent = new HashSet<Message>();
		this.logger = Logger.getLogger(name);
	}

	public Set<SiteItf> getChildren() throws RemoteException {
		return this.children;
	}

	public boolean addChild(SiteItf child) throws RemoteException {
		if (child == null) {
			throw new IllegalArgumentException("child argument cannot be null!");
		}
		return this.children.add(child);
	}

	public synchronized void propagate() throws RemoteException {
		for (Message message : this.received) {
			for (SiteItf child : this.children) {
				child.receive(message);
			}
			this.sent.add(message);
		}
		this.received.clear();
	}

	public boolean receive(final Message message) throws RemoteException {
		if (message == null) {
			throw new IllegalArgumentException(
					"message argument cannot be null");
		}
		if (this.received.contains(message)) {
			return false;
		}
		if (this.sent.contains(message)) {
			return false;
		}

		logger.log(Level.INFO, "RECEIVED : " + message.getContent() + " (from "
				+ message.getSender().getName() + ")");
		Runnable propagator = new Runnable() {
			public void run() {
				try {
					SiteImpl.this.propagate();
				} catch (RemoteException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
					return;
				}
			}
		};

		boolean result = this.received.add(message);
		if (result) {
			Thread t = new Thread(propagator);
			t.start();
		}
		return result;
	}

	public String getName() throws RemoteException {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SiteImpl other = (SiteImpl) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public boolean hasUnsentMessages() throws RemoteException {
		return !this.received.isEmpty();
	}

}
