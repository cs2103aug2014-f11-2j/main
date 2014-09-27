package cs2103;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class CommandExecutorTest {

	@Test
	public void testStringToDate() {
		String result = "2008/01/02/23:45 to 2008/04/12/12:24";
		String[] time = new String[2];
		time[0]=null; time[1]=null;
		Pattern p = Pattern.compile("\\d{4}/\\d{2}/\\d{2}/\\d{2}:\\d{2}");
		Matcher m = p.matcher(result);
		int i = 0;
		while(m.find()){
			time[i] = m.group();
			i++;
		}
		for (String s:time){
			System.out.println(s);
		}
	}

}
