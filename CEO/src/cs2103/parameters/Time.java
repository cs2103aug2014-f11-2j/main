package cs2103.parameters;

import java.util.Date;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class Time implements Parameter {
	public static final String[] allowedLiteral = {"T", "time"};
	public static final String type = "TIME";
	private final Date[] time;
	
	public Time(Date[] time){
		this.time = time;
	}
	
	public Date[] getValue(){
		return this.time;
	}
	
	@Override
	public String getType() {
		return type;
	}

	public static Time parse(String timeString){
		if (timeString == null){
			return null;
		} else {
			return new Time(parseTime(timeString));
		}
	}
	
	private static Date[] parseTime(String timeString){
		Date[] time = new Date[2];
		if (timeString != null){
			java.util.List<Date> dates = new PrettyTimeParser().parse(timeString);
			if (!dates.isEmpty()){
				for (int i = 0;i < dates.size() && i < 2;i++){
					time[i] = dates.get(i);
				}
			}
		}
		return time;
	}
}
