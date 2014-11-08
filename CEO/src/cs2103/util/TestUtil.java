//@author A0110906R
package cs2103.util;

import cs2103.task.*;

/**
 * @author Zheng Han
 * Used for testing by comparing tasks
 */
public class TestUtil {
	
	/**
	 * Compares the Task objects if they are similar, returns true, 
	 * false otherwise.
	 * 
	 * @param task1
	 * @param task2
	 * @return a true value if Task arguments are similar, returns false otherwise
	 */
	public static boolean compareTasks(Task task1, Task task2) {
		if (task1 == null || task2 == null) return false;
		if (!task1.getTitle().equals(task2.getTitle())) return false;
		if (!task1.getDescription().equals(task2.getDescription())) return false;
		if (task1.getCreated().getTime() != task2.getCreated().getTime()) return false;
		if (!task1.getStatus().equals(task2.getStatus())) return false;
		if (task1.getLastModified().getTime() != task2.getLastModified().getTime()) return false;
		if (task1 instanceof ToDoTask && task2 instanceof ToDoTask) {
			return compareToDo((ToDoTask) task1, (ToDoTask) task2);
		} else if (task1 instanceof EventTask && task2 instanceof EventTask) {
			return compareEvent((EventTask) task1, (EventTask) task2);
		} else {
			return false;
		}
	}
	
	private static boolean compareToDo(ToDoTask task1, ToDoTask task2) {
		if (task1 == null || task2 == null) return false;
		if (task1.getCompleted() == null ^ task2.getCompleted() == null) return false;
		if (task1.getCompleted() != null && task2.getCompleted() != null) {
			if (task1.getCompleted().getTime() != task2.getCompleted().getTime()) return false;
		}
		if (task1 instanceof DeadlineTask ^ task2 instanceof DeadlineTask) return false;
		if (task1 instanceof DeadlineTask && task2 instanceof DeadlineTask) {
			if (((DeadlineTask) task1).getDueTime().getTime() != ((DeadlineTask) task2).getDueTime().getTime()) return false;
		}
		return true;
	}
	
	private static boolean compareEvent(EventTask task1, EventTask task2) {
		if (task1 == null || task2 == null) return false;
		if (task1.getStartTime().getTime() != task2.getStartTime().getTime()) return false;
		if (task1.getEndTime().getTime() != task2.getEndTime().getTime()) return false;
		if (task1 instanceof PeriodicTask ^ task2 instanceof PeriodicTask) return false;
		if (task1 instanceof PeriodicTask && task2 instanceof PeriodicTask) {
			if (!((PeriodicTask) task1).getLocation().equals(((PeriodicTask) task2).getLocation())) return false;
			if (((PeriodicTask) task1).getRecurrence() == null ^ ((PeriodicTask) task2).getRecurrence() == null) return false;
			if (((PeriodicTask) task1).getRecurrence() != null && ((PeriodicTask) task2).getRecurrence() != null) {
				if (!((PeriodicTask) task1).getRecurrence().toString().equals(((PeriodicTask) task2).getRecurrence().toString())) return false;
			}
		}
		return true;
	}
}
