package cs2103;

import cs2103.task.*;

public class TestUtil {
	public static boolean compareTasks(Task task1, Task task2){
		if (task1 == null || task2 == null) return false;
		if (!task1.getTitle().equals(task2.getTitle())) return false;
		if (!task1.getDescription().equals(task2.getDescription())) return false;
		if (task1.getCreated().getTime() != task2.getCreated().getTime()) return false;
		if (task1.getLastModified().getTime() != task2.getLastModified().getTime()) return false;
		if (task1 instanceof FloatingTask && task2 instanceof FloatingTask){
			return compareFloating((FloatingTask) task1, (FloatingTask) task2);
		} else if (task1 instanceof DeadlineTask && task2 instanceof DeadlineTask){
			return compareDeadline((DeadlineTask) task1, (DeadlineTask) task2);
		} else if (task1 instanceof PeriodicTask && task2 instanceof PeriodicTask){
			return comparePeriodic((PeriodicTask) task1, (PeriodicTask) task2);
		} else {
			return false;
		}
	}
	
	private static boolean compareFloating(FloatingTask task1, FloatingTask task2){
		if (task1 == null || task2 == null) return false;
		if (task1.getComplete() != task2.getComplete()) return false;
		return true;
	}
	
	private static boolean compareDeadline(DeadlineTask task1, DeadlineTask task2){
		if (task1 == null || task2 == null) return false;
		if (task1.getComplete() != task2.getComplete()) return false;
		if (task1.getDueTime().getTime() != task2.getDueTime().getTime()) return false;
		return true;
	}
	
	private static boolean comparePeriodic(PeriodicTask task1, PeriodicTask task2){
		if (task1 == null || task2 == null) return false;
		if (!task1.getLocation().equals(task2.getLocation())) return false;
		if (task1.getStartTime().getTime() != task2.getStartTime().getTime()) return false;
		if (task1.getEndTime().getTime() != task2.getEndTime().getTime()) return false;
		if (task1.getRecurrence() == null && task2.getRecurrence() == null){
			return true;
		} else if (task1.getRecurrence() == null || task2.getRecurrence() == null){
			return false;
		} else {
			return task1.getRecurrence().toString().equals(task2.getRecurrence().toString());
		}
	}
}
