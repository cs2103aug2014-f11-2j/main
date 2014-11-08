//@author A0112673L
package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

public class LocationTest {

	@Test
	public void testLocation() {
		Location loc = Location.parse("Hougang,Singapore");
		assertEquals("Hougang,Singapore",loc.getValue());
		assertEquals("LOCATION",loc.getType());
		
	}

}
