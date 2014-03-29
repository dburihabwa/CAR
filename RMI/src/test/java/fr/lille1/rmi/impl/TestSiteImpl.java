package fr.lille1.rmi.impl;

import static org.junit.Assert.fail;

import java.rmi.RemoteException;

import org.junit.Assert;
import org.junit.Test;

import fr.lille1.car.rmi.impl.SiteImpl;
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
	public void testSetNullParent() throws RemoteException {
		SiteItf site = new SiteImpl("test");
		site.setParent(null);
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

}
