package cs2103.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cs2103.exception.HandledException;

public class ParameterList {
	private Map<String, Parameter> parameterMap;
	
	public ParameterList(){
		parameterMap = new HashMap<String, Parameter>();
	}
	
	/**
	 * @param typeString
	 * @param type
	 * @return Parameter object that matches String typeString and type
	 * @throws HandledException
	 */
	public <T extends Parameter> T getParameter(String typeString, Class<T> type) throws HandledException{
		if (this.parameterMap.containsKey(typeString)){
			Parameter parameter = this.parameterMap.get(typeString);
			assert(parameter != null);
			if (parameter.getClass() == type){
				return type.cast(parameter);
			} else {
				throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Adds parameter into parameterMap
	 * @param parameter
	 */
	public void addParameter(Parameter parameter){
		if (parameter != null){
			parameterMap.put(parameter.getType(), parameter);
		}
	}
	
	/**
	 * Adds parameters from parameterList to parameterMap
	 * @param parameterList
	 */
	public void addAllParameters(ArrayList<Parameter> parameterList){
		if (parameterList != null){
			for (Parameter parameter:parameterList){
				if (parameter != null){
					this.addParameter(parameter);
				}
			}
		}
	}
	
	/**
	 * @return int size of parameterMap
	 */
	public int getParameterCount(){
		return this.parameterMap.size();
	}
	
	/**
	 * @return Title object from ParameterMap
	 * @throws HandledException
	 */
	public Title getTitle() throws HandledException{
		return this.getParameter(Title.type, Title.class);
	}
	
	/**
	 * @return Description object from ParameterMap
	 * @throws HandledException
	 */
	public Description getDescription() throws HandledException{
		return this.getParameter(Description.type, Description.class);
	}
	
	/**
	 * @return Location object from ParameterMap
	 * @throws HandledException
	 */
	public Location getLocation() throws HandledException{
		return this.getParameter(Location.type, Location.class);
	}
	
	/**
	 * @return Time object from ParameterMap
	 * @throws HandledException
	 */
	public Time getTime() throws HandledException{
		return this.getParameter(Time.type, Time.class);
	}
	
	/**
	 * @return Complete object from ParameterMap
	 * @throws HandledException
	 */
	public Complete getComplete() throws HandledException{
		return this.getParameter(Complete.type, Complete.class);
	}
	
	/**
	 * @return Recurrence object from ParameterMap
	 * @throws HandledException
	 */
	public Recurrence getRecurrence() throws HandledException{
		return this.getParameter(Recurrence.type, Recurrence.class);
	}
	
	/**
	 * @return TaskID object from ParameterMap
	 * @throws HandledException
	 */
	public TaskID getTaskID() throws HandledException{
		return this.getParameter(TaskID.type, TaskID.class);
	}
	
	public TaskType getTaskType() throws HandledException{
		return this.getParameter(TaskType.type, TaskType.class);
	}
	
	/**
	 * @return CommandType object from ParameterMap
	 * @throws HandledException
	 */
	public CommandType getCommandType() throws HandledException{
		return this.getParameter(CommandType.type, CommandType.class);
	}
	
	/**
	 * @return Keyword object from ParameterMap
	 * @throws HandledException
	 */
	public Keyword getKeyword() throws HandledException{
		return this.getParameter(Keyword.type, Keyword.class);
	}
	
	/**
	 * @return Delete object from ParameterMap
	 * @throws HandledException
	 */
	public DeleteOption getDeleteOption() throws HandledException{
		return this.getParameter(DeleteOption.type, DeleteOption.class);
	}
	
	/**
	 * @return Option object from ParameterMap
	 * @throws HandledException
	 */
	public Option getOption() throws HandledException{
		return this.getParameter(Option.type, Option.class);
	}
}
