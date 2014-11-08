//@author A0112673L
package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeywordTest {

	@Test
	public void testKeyword() {
		Keyword kw = Keyword.parse("Utown");
		assertEquals("Utown", kw.getValue());
		assertEquals("KEYWORD",kw.getType());
		
		kw = Keyword.parse(null);
		assertNull(kw);
	}

}
