//@author A0112673L
package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.junit.Assert.*;

import org.fusesource.jansi.Ansi;
import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;
import cs2103.task.DeadlineTask;
import cs2103.task.PeriodicTask;
import cs2103.parameters.ParameterList;
import cs2103.parameters.TaskType.Value;

public class SearchTest {
	
	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
		Add addObj = new Add("-title Jap homework -time 2014/10/23 21:00");
		addObj.execute();
		addObj = new Add("-title revise stats homework -description revise chp 5 to 7 -location NUS UTOWN -time 2014/10/24 20:00 to 2014/10/25 20:00");
		addObj.execute();
		addObj = new Add("-title celebrate 21st :D -time 2014/11/1 18:00");
		addObj.execute();
		addObj = new Add("-title do cs2105 networking assignment homework");
		addObj.execute();
		addObj = new Add("-title finish essay");
		addObj.execute();
		addObj = new Add("-title complete");
		addObj.execute();
		Mark m = new Mark("6");
		m.execute();
		m = new Mark("3");
		m.execute();
	}
		
	@Test
	public void testSearchKeyword() throws HandledException, FatalException {	
		assert(TaskList.getInstance()!=null);
		Search searchTitle = new Search("homework -time");
		ParameterList pl = searchTitle.getParameterList();
		assertEquals("homework",pl.getKeyword().getValue());
		assertEquals(Value.DEFAULT,pl.getTaskType().getValue());
		String result = searchTitle.execute().toString();
		Ansi deadTaskHw = null;
		for(DeadlineTask t : TaskList.getInstance().getDeadlineList()){
			if(t.getTitle().contains("homework")){
				deadTaskHw = t.toSummary();
			}
		}
		PeriodicTask pt = TaskList.getInstance().getPeriodicList().get(0);
		assertEquals(ansi().a(deadTaskHw).a("\n").a(pt.toSummary()).a("\n").toString(),result);
	}
	
	@Test
	public void testSearchPeriodic() throws HandledException, FatalException{
		assert(TaskList.getInstance()!=null);
		Search searchDeadline = new Search("-type deadline -complete");
		ParameterList pl = searchDeadline.getParameterList();
		assertEquals(Value.DEADLINE,pl.getTaskType().getValue());
		String result = searchDeadline.execute().toString();
		Ansi completedDeadline = null;
		for(DeadlineTask t : TaskList.getInstance().getDeadlineList()){
			if(t.getCompleted()!=null){
				completedDeadline = t.toSummary();
			}
		}
		assertEquals(ansi().a(completedDeadline).a("\n").toString(),result);
	}
	
	@Test
	public void testSearchNonExistent() throws HandledException, FatalException{
		assert(TaskList.getInstance()!=null);
		Search searchNonExistent = new Search("presentation");
		String result = searchNonExistent.execute().toString();
		assertEquals(ansi().bold().fg(RED).a("The task list is empty\n").reset().toString(),result);
	}
}
