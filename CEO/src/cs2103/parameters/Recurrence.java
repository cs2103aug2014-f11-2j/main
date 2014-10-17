package cs2103.parameters;

import net.fortuna.ical4j.model.Recur;

public class Recurrence implements Parameter {
	public static final String type = "RECURRENCE";
	private final Recur recur;
	
	public Recurrence(Recur recur){
		this.recur = recur;
	}
	
	public Recur getRecurrence(){
		return this.recur;
	}

	@Override
	public String getType() {
		return type;
	}
}
