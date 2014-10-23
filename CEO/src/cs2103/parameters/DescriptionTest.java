package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

public class DescriptionTest {

	@Test
	public void testDescription() {
		Description d = Description.parse("this is another desc");
		assertEquals("this is another desc",d.getValue());
		assertEquals("DESCRIPTION",d.getType());
		
	}

}
