package fr.lille1.car.rmi.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import fr.lille1.car.rmi.interfaces.Message;
import fr.lille1.car.rmi.interfaces.SiteItf;

public class MessageImpl extends UnicastRemoteObject implements Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1890967346412897998L;
	private final SiteItf sender;
	private final String content;
	private final long time;

	public MessageImpl(final SiteItf sender, final String content)
			throws RemoteException {
		super();
		if (sender == null) {
			throw new IllegalArgumentException(
					"sender argument cannot be null!");
		}
		if (content == null) {
			throw new IllegalArgumentException(
					"content argument cannot be null!");
		}
		this.sender = sender;
		this.content = content;
		this.time = System.nanoTime();
	}

	public SiteItf getSender() throws RemoteException {
		return this.sender;
	}

	public String getContent() throws RemoteException {
		return this.content;
	}

	public long getTime() throws RemoteException {
		return this.time;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MessageImpl)) {
			return false;
		}
		MessageImpl other = (MessageImpl) obj;
		if (time != other.time) {
			return false;
		}
		if (sender == null) {
			if (other.sender != null) {
				return false;
			}
		} else if (!sender.equals(other.sender)) {
			return false;
		}
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		return true;
	}

}
