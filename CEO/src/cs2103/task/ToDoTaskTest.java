//@author A0128478R
package cs2103.task;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.junit.Assert.*;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.Status;

import org.junit.Test;

/**
 *	Containing tests for the inherited methods in 
 * 				concrete Task classes: DeadlineTask and FloatingTask
 */
public abstract class ToDoTaskTest extends TaskTest{

	@Test
	public void testUpdateAndGetCompleted(){
		ToDoTask task = (ToDoTask) getConcrete();
		task.updateCompleted(null);
		assertEquals(null, task.getCompleted());
		
		DateTime testDate = new DateTime();
		task.updateCompleted(testDate);
		assertTrue(testDate.equals(task.getCompleted()));
	}
	
	@Test
	public void testUpdateAndGetStatus(){
		ToDoTask task = (ToDoTask) getConcrete();
		task.updateStatus(null);
		assertEquals(Status.VTODO_NEEDS_ACTION, task.getStatus());
		
		Status testStatus = Status.VTODO_COMPLETED;
		task.updateStatus(testStatus);
		assertEquals(testStatus, task.getStatus());
	}
	
	@Test
	public void testRestore(){
		ToDoTask task = (ToDoTask) getConcrete();
		task.updateCompleted(null);
		task.restore();
		assertEquals(Status.VTODO_NEEDS_ACTION, task.getStatus());
		
		DateTime testDate = new DateTime();
		task.updateCompleted(testDate);
		task.restore();
		assertEquals(Status.VTODO_COMPLETED, task.getStatus());
		assertEquals(new DateTime(), task.getLastModified());
	}
	
	@Test
	public void testCompletedToString(){
		Ansi test = ToDoTask.completedToString(null);
		Ansi expected = generateCompletedToStringExpectedNeedsAction();
		assertTrue(test.toString().equals(expected.toString()));
		
		DateTime testDate = new DateTime();
		test = ToDoTask.completedToString(testDate);
		expected = generateCompletedToStringExpectedCompleted(); 
		assertTrue(test.toString().equals(expected.toString()));
	}

	private Ansi generateCompletedToStringExpectedCompleted() {
		Ansi expected;
		expected = ansi().bold().fg(GREEN).a("Completed").reset();
		return expected;
	}

	private Ansi generateCompletedToStringExpectedNeedsAction() {
		Ansi expected = ansi().bold().fg(RED).a("Needs Action").reset();
		return expected;
	}
	
	/**
	 * Matches returns true if the input string is included, regardless
	 * 				of case, in the title or description fields 
	 */
	@Test
	public void testMatches(){
		ToDoTask task = (ToDoTask) getConcrete();
		testMatchesHelper(task);
	}

	private void testMatchesHelper(ToDoTask task) {
		testMatchesEmptySearch(task);
		testMatchesTitle(task);
		testMatchesFail(task);
		testMatchesDescription(task);
	}

	private void testMatchesEmptySearch(ToDoTask task) {
		String testKeyword = null;
		assertTrue(task.matches(testKeyword));
		
		testKeyword = "";
		assertTrue(task.matches(testKeyword));
	}

	private void testMatchesFail(ToDoTask task) {
		String testKeyword;
		testKeyword = "Testing";
		assertTrue(!(task.matches(testKeyword)));
	}

	private void testMatchesTitle(ToDoTask task) {
		String testKeyword;
		testKeyword = "teSt";
		task.updateTitle("Test Title");
		assertTrue(task.matches(testKeyword));
	}

	private void testMatchesDescription(ToDoTask task) {
		String testKeyword;
		testKeyword = "DescRiption";
		task.updateDescription("Test Description");
		assertTrue(task.matches(testKeyword));
	}	
	
	@Test 
	public void testToDetail(){
		ToDoTask task = (ToDoTask) getConcrete();
		Ansi expected = generateDetailExpected(task);
		Ansi test = task.toDetail();
		assertEquals(expected.toString(), test.toString());
	}

	private Ansi generateDetailExpected(ToDoTask task) {
		Ansi expected = task.toSummary();
		expected.a("Description: ").a(task.getDescription()).a('\n').reset();
		return expected;
	}
}