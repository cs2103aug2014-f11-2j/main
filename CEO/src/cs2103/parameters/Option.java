package cs2103.parameters;

public class Option implements Parameter {
	public static final String type = "OPTIONS";
	public static enum Value {
		DEFAULT, NOSYNC, TEST;
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
	
	public static Option parse(String optionString){
		return new Option(parseOption(optionString));
	}
	
	private static Value parseOption(String optionString){
		if (optionString == null){
			return Value.DEFAULT;
		}
		if (optionString.equalsIgnoreCase("nosync") || optionString.equalsIgnoreCase("no-sync") || optionString.equalsIgnoreCase("disable-sync")){
			return Value.NOSYNC;
		} else if (optionString.equalsIgnoreCase("test")){
			return Value.TEST;
		} else {
			return Value.DEFAULT;
		}
	}
}
