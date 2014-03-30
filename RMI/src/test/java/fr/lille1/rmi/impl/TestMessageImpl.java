package fr.lille1.rmi.impl;

import static org.junit.Assert.fail;

import java.rmi.RemoteException;

import org.junit.Assert;
import org.junit.Test;

import fr.lille1.car.rmi.impl.MessageImpl;
import fr.lille1.car.rmi.impl.SiteImpl;
import fr.lille1.car.rmi.interfaces.Message;
import fr.lille1.car.rmi.interfaces.SiteItf;

public class TestMessageImpl {

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNullSender() throws RemoteException {
		new MessageImpl(null, "content");
		fail("An exception should have been raised before reaching this line!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNullContent() throws RemoteException {
		new MessageImpl(new SiteImpl("sender"), null);
		fail("An exception should have been raised before reaching this line!");
	}

	@Test
	public void testEqualsDifferentTime() throws RemoteException {
		SiteItf sender = new SiteImpl("sender");
		String content = "content";
		Message message1 = new MessageImpl(sender, content);
		Message message2 = new MessageImpl(sender, content);
		Assert.assertFalse(message1.equals(message2));
	}

	@Test
	public void testEquals() throws RemoteException {
		SiteItf sender = new SiteImpl("sender");
		String content = "content";
		Message message1 = new MessageImpl(sender, content);
		Assert.assertTrue(message1.equals(message1));
	}
}
