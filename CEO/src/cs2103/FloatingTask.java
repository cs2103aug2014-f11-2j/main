package cs2103;

class FloatingTask extends Task {
	private String progress;
	private final static String INCOMPLETE="incomplete";
	private final static String IN_PROCESS="in_process";
	private final static String COMPLETED="completed";
	
	public FloatingTask(String taskUID, String title, String progress) throws CEOException {
		super(taskUID, title);
		this.updateProgress(progress);
	}
	
	public String getProgress(){
		return this.progress;
	}
	
	public void updateProgress(String progress) throws CEOException{
		if (progress.equals(INCOMPLETE) || progress.equals(IN_PROCESS) || progress.equals(COMPLETED)){
			this.progress=progress;
		}else{
			throw new CEOException("Invalid Progress");
		}
	}
}
