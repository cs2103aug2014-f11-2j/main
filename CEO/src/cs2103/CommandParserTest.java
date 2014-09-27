package cs2103;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



public class CommandParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSeperateCommandNoParameters() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("");
		actual = CommandParser.seperateCommand("");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSeperateCommandOneParameter() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("add");
		actual = CommandParser.seperateCommand("add");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSeperateCommandTwoParameters() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("add");
		expected.add("two");
		actual = CommandParser.seperateCommand("add two");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSeperateCommandTwoParametersMultipleSpaces() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("add");
		expected.add("two");
		actual = CommandParser.seperateCommand("add      two  ");
		assertEquals(expected, actual);
	}

	
	

}
