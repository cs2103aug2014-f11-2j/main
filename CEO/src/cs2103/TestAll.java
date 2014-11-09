//@author A0116713M
package cs2103;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import cs2103.command.*;
import cs2103.parameters.*;
import cs2103.task.*;
import cs2103.storage.*;
import cs2103.util.*;

@RunWith(Suite.class)
@SuiteClasses({CommandLineUITest.class,
	AddTest.class, AlertTest.class, DeleteTest.class, HelpTest.class, ListTest.class, MarkTest.class, RestoreTest.class, SearchTest.class, ShowTest.class, SyncTest.class, UpdateTest.class, UpdateTimeFromRecurTest.class,
	CommandTypeTest.class, CompleteTest.class, DeleteOptionTest.class, DescriptionTest.class, KeywordTest.class, LocationTest.class, OptionTest.class, ParameterListTest.class, RecurrenceTest.class, TaskIDTest.class, TaskTypeTest.class, TimeTest.class, TitleTest.class,
	DeadlineTaskTest.class, FloatingTaskTest.class, PeriodicTaskTest.class,
	StorageEngineTest.class, GoogleEngineTest.class, TaskListTest.class,
	CommonUtilTest.class})
public class TestAll {

}
