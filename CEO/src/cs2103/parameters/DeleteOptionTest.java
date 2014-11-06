package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

public class DeleteOptionTest {

	@Test
	public void testDelete() {
		DeleteOption dOption = DeleteOption.parse("");
		assertTrue(dOption.getValue());
		
		dOption = DeleteOption.parse(null);
		assertNull(dOption);
		
	}

}
