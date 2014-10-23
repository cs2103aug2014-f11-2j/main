package cs2103.parameters;

import cs2103.CommonUtil;
import cs2103.exception.HandledException;

public class Option implements Parameter {
	public static final String type = "OPTIONS";
	public static enum Value {
		DEFAULT, SYNC, NOSYNC, TEST;
	}
	private final Value value;
	
	public Option(Value value){
		this.value = value;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public Value getValue(){
		return this.value;
	}
	
	public static Option parse(String[] optionString) throws HandledException{
		return new Option(parseOption(optionString));
	}
	
	private static Value parseOption(String[] optionString) throws HandledException{
		if (optionString == null || optionString.length < 1){
			return Value.DEFAULT;
		} else {
			String option = CommonUtil.removeDash(optionString[0]);
			if (option.equalsIgnoreCase("nosync") || option.equalsIgnoreCase("no-sync") || option.equalsIgnoreCase("disable-sync")){
				return Value.NOSYNC;
			} else if (option.equalsIgnoreCase("test")){
				return Value.TEST;
			} else if (option.equalsIgnoreCase("sync") || option.equalsIgnoreCase("enable-sync")){
				return Value.SYNC;
			} else {
				return Value.DEFAULT;
			}
		}
	}
}
