package cs2103;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import net.fortuna.ical4j.model.component.VToDo;

import org.junit.Test;

public class StorageEngineTest {

	@Test
	public void test() {
		StorageEngine test=new StorageEngine("test");
		try {
			ArrayList<Task> taskList=test.getTaskList();
			Task task = new PeriodicTask(null, "test", new Date(), new Date());
			task.updateDescription("");
			task.updateLocation("");
			test.updateTask(task);
			//test.deleteTask(taskList.get(0));
		} catch (CEOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
