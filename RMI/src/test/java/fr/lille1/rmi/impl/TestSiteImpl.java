package fr.lille1.rmi.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.Assert;
import org.junit.Test;

import fr.lille1.car.rmi.impl.SiteImpl;
import fr.lille1.car.rmi.interfaces.Message;
import fr.lille1.car.rmi.interfaces.SiteItf;

public class TestSiteImpl {

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNullName() throws RemoteException {
		new SiteImpl(null);
		fail("An exception should have been raised before reaching this line!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetNullMessage() throws RemoteException {
		SiteItf site = new SiteImpl("test");
		site.setMessage(null);
		fail("An exception should have been raised before reaching this line!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testaddNullChild() throws RemoteException {
		SiteItf site = new SiteImpl("test");
		site.addChild(null);
		fail("An exception should have been raised before reaching this line!");
	}

	@Test
	public void testEquals() throws RemoteException {
		SiteItf site = new SiteImpl("test");
		Assert.assertTrue(site.equals(site));
	}

	@Test
	public void testPassingMessages() throws RemoteException,
			AlreadyBoundException, NotBoundException {
		SiteItf site1 = new SiteImpl("site1"), site2 = new SiteImpl("site2");
		Message message = mock(Message.class);
		when(message.getSender()).thenReturn(site1);
		when(message.getContent()).thenReturn("content");
		when(message.getTime()).thenReturn(1000L);
		assertFalse(site1.hasUnsentMessages());
		assertFalse(site2.hasUnsentMessages());
		site1.addChild(site2);
		site1.setMessage(message);
		assertTrue(site1.hasUnsentMessages());
		assertFalse(site2.hasUnsentMessages());
		site1.propagate();
		assertFalse(site1.hasUnsentMessages());
		assertTrue(site2.hasUnsentMessages());
	}

	@Test
	public void testBlockingAlreadyReceivedMessages() throws RemoteException,
			AlreadyBoundException, NotBoundException {
		SiteItf site1 = new SiteImpl("site1");
		Message message = mock(Message.class);
		when(message.getSender()).thenReturn(site1);
		when(message.getContent()).thenReturn("content");
		when(message.getTime()).thenReturn(1000L);
		assertTrue(site1.setMessage(message));
		assertFalse(site1.setMessage(message));
	}

	@Test
	public void testBlockingAlreadySentMessages() throws RemoteException,
			AlreadyBoundException, NotBoundException {
		SiteItf site1 = new SiteImpl("site1");
		Message message = mock(Message.class);
		when(message.getSender()).thenReturn(site1);
		when(message.getContent()).thenReturn("content");
		when(message.getTime()).thenReturn(1000L);
		assertTrue(site1.setMessage(message));
		site1.propagate();
		assertFalse(site1.hasUnsentMessages());
		assertFalse(site1.setMessage(message));
	}
}
