package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;
import cs2103.task.Task;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlertTest {
	
	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));		
	}
	
	@Test
	public void test1_BeforeSystemDate_Alert() throws HandledException, FatalException {
		Date dt = new Date();
		Calendar cBefore_from = Calendar.getInstance(); 
		cBefore_from.setTime(dt); 
		cBefore_from.add(Calendar.DATE, -1);
		dt = cBefore_from.getTime();
		
		Date dt_to = new Date();
		Calendar cBefore_to = Calendar.getInstance(); 
		cBefore_to.setTime(dt_to); 
		dt_to = cBefore_to.getTime();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Alert alert = new Alert();
		
		Add addobj = new Add("add -title testDeadlineAlert -time "+dateFormat.format(dt));
		addobj.execute();
		addobj = new Add("add -title testPeriodicAlert -time "+dateFormat.format(dt)+" to "+dateFormat.format(dt_to));
		addobj.execute();	
		String result = alert.execute().toString();
	
		assertEquals(ansi().a("Tasks due within one day:\n").bold().fg(RED).a("The task list is empty\n").reset()
				.a("Tasks start within one day:\n").bold().fg(RED).a("The task list is empty\n").reset().toString(),result);
	}
	
	@Test
	public void test2_WithinOneDay_Alert() throws HandledException, FatalException {
		Date dt = new Date();
		Calendar cDuring_from = Calendar.getInstance(); 
		cDuring_from.setTime(dt); 
		cDuring_from.add(Calendar.HOUR_OF_DAY, 1);
		dt = cDuring_from.getTime();
		
		Date dt_to = new Date();
		Calendar cDuring_to = Calendar.getInstance(); 
		cDuring_to.setTime(dt_to); 
		cDuring_to.add(Calendar.DATE, 1);
		dt_to = cDuring_to.getTime();
		
		Date[]deadlineTask_time = new Date[2];
		deadlineTask_time[0] = dt;
	
		Date[]periodicTask_time = new Date[2];
		periodicTask_time[0] = dt;
		periodicTask_time[1] = dt_to;
		
		Alert alert = new Alert();
		Task dead = TaskList.getInstance().getDeadlineList().get(0);
		TaskList.getInstance().updateTask(dead.update(deadlineTask_time));
		dead = TaskList.getInstance().getDeadlineList().get(0);
		
		Task period = TaskList.getInstance().getPeriodicList().get(0);
		TaskList.getInstance().updateTask(period.update(periodicTask_time));
		period = TaskList.getInstance().getPeriodicList().get(0);
		
		boolean check = dead.checkAlert();
		assertTrue(check);
		check = period.checkAlert();
		assertTrue(check);
		
		String result = alert.execute().toString();
		
		assertEquals(ansi().a("Tasks due within one day:\n").a(dead.toSummary()).a("\n")
				.a("Tasks start within one day:\n").a(period.toSummary()).a("\n").toString(),result);
	}
	
	@Test
	public void test3_MoreThanOneDay_Alert() throws HandledException, FatalException {
		Date dt = new Date();
		Calendar cAfter_from = Calendar.getInstance(); 
		cAfter_from.setTime(dt); 
		cAfter_from.add(Calendar.HOUR_OF_DAY, 25);
		dt = cAfter_from.getTime();
		
		Date dt_to = new Date();
		Calendar cAfter_TO = Calendar.getInstance(); 
		cAfter_TO.setTime(dt_to); 
		cAfter_TO.add(Calendar.HOUR_OF_DAY, 45);
		dt_to = cAfter_TO.getTime();
		
		Date[]deadlineTask_time = new Date[2];
		deadlineTask_time[0] = dt;
	
		Date[]periodicTask_time = new Date[2];
		periodicTask_time[0] = dt;
		periodicTask_time[1] = dt_to;
		
		Alert alert = new Alert();
		
		Task dead = TaskList.getInstance().getDeadlineList().get(0);
		TaskList.getInstance().updateTask(dead.update(deadlineTask_time));
		dead = TaskList.getInstance().getDeadlineList().get(0);
		
		Task period = TaskList.getInstance().getPeriodicList().get(0);
		TaskList.getInstance().updateTask(period.update(periodicTask_time));
		period = TaskList.getInstance().getPeriodicList().get(0);
		
		boolean check = dead.checkAlert();
		assertFalse(check);
		check = period.checkAlert();
		assertFalse(check);
		String result = alert.execute().toString();
		assertEquals(ansi().a("Tasks due within one day:\n").bold().fg(RED).a("The task list is empty\n").reset()
				.a("Tasks start within one day:\n").bold().fg(RED).a("The task list is empty\n").reset().toString(),result);
	}
}
