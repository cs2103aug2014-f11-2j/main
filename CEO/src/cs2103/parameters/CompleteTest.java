package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.exception.HandledException;

public class CompleteTest {

	@Test
	public void testComplete() throws HandledException {
		Complete c = new Complete(false);
		assertFalse(c.getValue());
		c = Complete.parse("true");
		assertTrue(c.getValue());
		
		c = Complete.parse("");
		assertTrue(c.getValue());
	}
	
	@SuppressWarnings("unused")
	@Test (expected = HandledException.class)
	public void testInvalidString() throws HandledException{
		Complete check = Complete.parse("hello");
	}
	

}
