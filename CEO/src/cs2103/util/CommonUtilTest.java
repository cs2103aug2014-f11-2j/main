package cs2103.util;

import static org.junit.Assert.*;

import java.lang.reflect.Array;

import org.junit.Test;

import cs2103.exception.HandledException;

public class CommonUtilTest {

	@Test(expected = HandledException.class)
	public void testSplitFirstWord() throws HandledException {
		String[] testing = CommonUtil.splitFirstWord("testing split");
		assertEquals(Array.get(testing, 0), "testing");
		assertEquals(Array.get(testing, 1), "split");
		
		String[] testing1 = CommonUtil.splitFirstWord("");
		assertEquals(Array.get(testing1, 0), "");
		assertNull(Array.get(testing1, 1));

		String[] testing2 = CommonUtil.splitFirstWord("123");
		assertEquals(Array.get(testing2, 0), "123");
		assertNull(Array.get(testing2, 1));
		
		CommonUtil.splitFirstWord(null);
	}
	
	
	@Test(expected = HandledException.class)
	public void testParseIntegerParameter() throws HandledException {
		int test = CommonUtil.parseIntegerParameter("1");
		assertEquals(test, 1);
		
		int test1 = CommonUtil.parseIntegerParameter("0");
		assertEquals(test1, 0);
		
		int test2 = CommonUtil.parseIntegerParameter("-2");
		assertEquals(test2,-1);
		
		int test3 = CommonUtil.parseIntegerParameter("0.1");
		assertEquals(test3, -1);
		
		int test4 = CommonUtil.parseIntegerParameter("-0.1");
		assertEquals(test4, -1);
		
		
		int test5 = CommonUtil.parseIntegerParameter("NOT AN INTEGER");
		assertEquals(test5, -1);
		
		CommonUtil.parseIntegerParameter(null);
	}
	
	@Test(expected = HandledException.class)
	public void testRemoveDash() throws HandledException {
		//test string with dash
		String test = CommonUtil.removeDash("-testing");
		assertEquals(test, "testing");
		
		//test string without dash
		String test1 = CommonUtil.removeDash("testing");
		assertEquals(test1, "testing");
		
		//test empty string
		String test2 = CommonUtil.removeDash("");
		assertEquals(test2, "");
		
		//test with dash only
		String test3 = CommonUtil.removeDash("-");
		assertEquals(test3, "");
		
		//test for null string
		CommonUtil.removeDash(null);
	}
}
