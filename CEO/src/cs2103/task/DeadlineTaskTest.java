//@author A0128478R
package cs2103.task;

import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.junit.Assert.*;
import java.util.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;
import org.junit.Before;
import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.util.TestUtil;

/**
 * @author brianluong
 *
 */
public class DeadlineTaskTest extends ToDoTaskTest{
	static DeadlineTask dlt;
	String taskUID = null;
	Date created = null; 
	Status status = null;
	String title = "Testing";
	String description = "Description";
	String location = "Location";
	Recur recurrence = null;
	Date complete = null;
	DateTime dueTime = new DateTime(1);
	
	protected DeadlineTask getConcrete(){
		return dlt;
	}

	@Before
	public void setUp() throws Exception {
		dlt = new DeadlineTask(this.taskUID, this.status, this.dueTime);
		dlt.updateTitle(this.title);
		dlt.updateDescription(this.description);
		dlt.updateLocation(this.location);
		dlt.updateRecurrence(this.recurrence);
		dlt.updateLastModified(null);
	}
	
	@Test 
	public void testDeadlineTaskConstructor() throws HandledException{
		testDeadlineTaskConstructionOne();
		testDeadlineTaskConstructionTwo();
	}
		
	public void testDeadlineTaskConstructionOne() throws HandledException {
		exception.expect(HandledException.class);
		dlt = new DeadlineTask(this.taskUID, this.status, null);
	}
	
	public void testDeadlineTaskConstructionTwo() throws HandledException {
		dlt = new DeadlineTask(this.taskUID, this.status, null);
		assertTrue(true);
	}

	@Test
	public void testUpdateAndGetDueTime() throws HandledException{
		DateTime newDate = new DateTime(1);
		assertTrue(dlt.getDueTime().compareTo(newDate) == 0);
	}

	@Test
	public void testCheckPeriod() {
		DateTime[] time = new DateTime[2];
		assertEquals(dlt.checkPeriod(time), true);
		
		time[0] = new DateTime(1);
		assertEquals(dlt.checkPeriod(time), false);
		
		time[0] = null;
		time[1] = new DateTime(2);
		assertEquals(dlt.checkPeriod(time), true);
	}

	@Test
	public void testConvert() throws HandledException {
		testConvertException();
		testConvertToFloating();
		testConvertToDeadline();
		testConvertToPeriodic();
	}
	
	private void testConvertException() throws HandledException {
		exception.expect(HandledException.class);
		DateTime[] time = null;
		dlt.convert(time);	
	}
	
	private void testConvertToFloating() throws HandledException {
		DateTime[] time = new DateTime[2];
		time[0] = null;
		time[1] = null;
		Task ft2 = dlt.convert(time);
		assertTrue(ft2 instanceof FloatingTask);
		
		Task taskExpected = new FloatingTask(dlt.getTaskUID(), Status.VTODO_NEEDS_ACTION);
		taskExpected.updateTitle(dlt.getTitle());
		taskExpected.updateDescription(dlt.getDescription());
		taskExpected.updateLastModified(dlt.getLastModified());
		taskExpected.updateCompleted(dlt.getCompleted());
		assertTrue(TestUtil.compareTasks(ft2, taskExpected));
	}

	private void testConvertToDeadline() throws HandledException {	
		DateTime[] time = new DateTime[2];
		time[0] = new DateTime(1);
		time[1] = null;
		Task ft2 = dlt.convert(time);
		assertTrue(ft2 instanceof DeadlineTask);
		
		DeadlineTask taskExpected = new DeadlineTask(dlt.getTaskUID(), dlt.getStatus(), time[0]);
		taskExpected.updateTitle(dlt.getTitle());
		taskExpected.updateDescription(dlt.getDescription());
		taskExpected.updateLastModified(dlt.getLastModified());
		taskExpected.updateCompleted(dlt.getCompleted());
		assertTrue(TestUtil.compareTasks(ft2, taskExpected));
	}
	
	private void testConvertToPeriodic() throws HandledException {
		DateTime[] time = new DateTime[2];
		time[0] = new DateTime(1);
		time[1]= new DateTime(2);
		Task ft2 = dlt.convert(time);
		assertTrue(ft2 instanceof PeriodicTask);
		
		PeriodicTask taskExpected = new PeriodicTask(dlt.getTaskUID(), Status.VEVENT_CONFIRMED, time[0], time[1]);
		taskExpected.updateTitle(dlt.getTitle());
		taskExpected.updateDescription(dlt.getDescription());
		taskExpected.updateLastModified(dlt.getLastModified());
		taskExpected.updateCompleted(dlt.getCompleted());
		assertTrue(TestUtil.compareTasks(ft2, taskExpected));
	}
	
	@Test
	public void testClone() throws CloneNotSupportedException {
		DeadlineTask task = (DeadlineTask) dlt.clone();
		assertTrue(TestUtil.compareTasks(task, dlt));
	}
	
	@Test
	public void testToSummary() {
		Ansi expected = ansi().fg(YELLOW).a(dlt.getTaskID()).a(". ").reset();
		expected.bold().a(dlt.getTitle()).a('\n').boldOff().reset();
		expected.a("Status: ").a(ToDoTask.completedToString(dlt.getCompleted()));
		expected.a("\tDue At: ").a(dlt.dateToString(dlt.getDueTime())).a('\n');
		Ansi test = dlt.toSummary();
		assertEquals(expected.toString(), test.toString());
	}
}