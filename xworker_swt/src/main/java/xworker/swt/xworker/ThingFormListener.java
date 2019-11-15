package xworker.swt.xworker;

public interface ThingFormListener {
	/**
	 * 表单已修改的事件。
	 * 
	 * @param thingForm
	 */
	public void modified(ThingForm thingForm);

	/**
	 * 默认选的事件。
	 * 
	 * @param thingForm
	 */
	public void defaultSelection(ThingForm thingForm);
	
}
