package cs2103.parameters;

import java.util.Date;

public class Time implements Parameter {
	public static final String type = "TIME";
	private final Date[] time;
	
	public Time(Date[] time){
		this.time = time;
	}
	
	public Date[] getTime(){
		return this.time;
	}
	
	@Override
	public String getType() {
		return type;
	}
}
