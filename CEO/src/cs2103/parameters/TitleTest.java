package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

public class TitleTest {

	@Test
	public void testTitle() {
		Title t = Title.parse("this is some title");
		assertEquals("this is some title",t.getValue());
		
		t = Title.parse(null);
		assertNull(t);
	}

}
