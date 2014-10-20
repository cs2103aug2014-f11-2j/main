package cs2103;

import static org.junit.Assert.*;

import java.lang.reflect.Array;

import org.junit.Test;

import cs2103.exception.HandledException;

public class CommonUtilTest {

	@Test
	public void testSplitFirstWord() throws HandledException {
		String[] testing = CommonUtil.splitFirstWord("testing split");
		assertEquals(Array.get(testing, 0), "testing");
		assertEquals(Array.get(testing, 1), "split");
	}

	@Test
	public void testParseIntegerParameter() throws HandledException {
		int test = CommonUtil.parseIntegerParameter("1");
		assertEquals(test, 1);
		
		int test2 = CommonUtil.parseIntegerParameter("NOT AN INTEGER");
		assertEquals(test2, -1);
	}

	@Test
	public void testDeleteLastChar() {
		StringBuffer testBuffer = new StringBuffer("testing");
		String test = CommonUtil.deleteLastChar(testBuffer);
		assertEquals(test, "testin");
	}

}
