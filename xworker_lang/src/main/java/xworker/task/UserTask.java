package xworker.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 可监控任务。
 * 
 * @author Administrator
 *
 */
public class UserTask {
	/** 等待执行 */
	public static final int WAIT = 0;
	/** 正在执行 */
	public static final int RUNING = 1;
	/** 已经执行结束 */
	public static final int FINISHED = 2;
	/** 已经终止 */
	public static final int TERMINATED = 3;
	/** 取消 */
	public static final int CANCEL = 4;
	
	int status = WAIT;
	
	/** 事件监听列表 */
	List<UserTaskListener> listeners = new ArrayList<UserTaskListener>();
	
	/** 任务事物 */
	Thing taskThing;
	
	/** 是否可以监控进度 */
	boolean progressAble;
	
	/** 进度 */
	int progress;
	
	/** 当前状态标签 */
	String currentLabel;
	
	/** 详细内容 */
	String detail;
	
	/** 通过jbos进度的一种方法。 */
	int totalJobs;
	int completeJobs;
	
	/** UserTask用户附加的属性 */
	Map<String, Object> datas = new HashMap<String, Object>();
	
	/** 变量上下文 */
	ActionContext actionContext;
	
	/**
	 * UserTask构造函数。
	 * 
	 * @param taskThing 任务事物
	 * @param progressAble 是否有进度条
	 */
	protected UserTask(Thing taskThing, boolean progressAble){
		this.taskThing = taskThing;
		this.progressAble = progressAble;
		this.detail = taskThing.getStringBlankAsNull("description");
		this.currentLabel = taskThing.getMetadata().getLabel();
	}
	
	/**
	 * 获取任务事物。
	 * 
	 * @return
	 */
	public Thing getTaskThing(){
		return taskThing;
	}
	
	public synchronized  void addListener(UserTaskListener listener){
		if(!listeners.contains(listener)){
			listeners.add(listener);
		}
		
		if(this.status == UserTask.FINISHED || this.status == UserTask.TERMINATED){
			listener.finished(this);
		}
	}
	
	public void removeListener(UserTaskListener listener){
		listeners.remove(listener);
	}
	
	/**
	 * 获取任务的名称。
	 * 
	 * @return
	 */
	public String getName(){
		return taskThing.getMetadata().getLabel();
	}
	
	public int getStatus(){
		return status;
	}
	
	/**
	 * 设置为取消的状态。
	 */
	public void cancel(){
		if(this.status != FINISHED && this.status != TERMINATED){
			this.status = CANCEL;
			
			this.taskThing.doAction("cancelUserTask", actionContext, "userTask", this);
		}
	}
	
	public String getCurrentLabel() {
		return currentLabel;
	}

	public void setCurrentLabel(String currentLabel) {
		this.currentLabel = currentLabel;
		
		for(UserTaskListener listener : listeners){
			listener.currentLabelUpdated(this, currentLabel);
		}
	}
	
	public void setLabel(String label){
		setCurrentLabel(label);
	}

	public String getLabel(){
		return getCurrentLabel();
	}
	
	public void set(String label, String detail) {
		this.setLabel(label);
		this.setDetail(detail);
	}
	
	/**
	 * 通知启动任务。
	 */
	public void start(){
		this.status = RUNING;
		
		for(UserTaskListener listener : listeners){
			listener.started(this);
		}
	}
	
	/**
	 * 设置进度条，为0-100之间的数字。
	 * 
	 * @param progress 进度
	 */
	public void setProgress(int progress){
		this.progress = progress;
		
		for(UserTaskListener listener : listeners){
			listener.progressSetted(this, progress);
		}
	}
	
	/**
	 * 获取当前进度。
	 * 
	 * @return
	 */
	public int getProgress(){
		return progress;
	}
	
	/**
	 * 是否有监控条。
	 * 
	 * @return
	 */
	public boolean isProgressAble(){
		return progressAble;
	}
	
	/**
	 * 已经结束。
	 * 
	 */
	public synchronized void finished(){
		if(this.status != UserTask.FINISHED && this.status != UserTask.TERMINATED){
			this.status = UserTask.FINISHED;
			
			for(UserTaskListener listener : listeners){
				listener.finished(this);
			}
		}
	}
	
	/**
	 * 已终止。
	 * 
	 */
	public synchronized void terminated(){
		if(this.status != UserTask.FINISHED && this.status != UserTask.TERMINATED){
			this.status = UserTask.TERMINATED;
			
			for(UserTaskListener listener : listeners){
				listener.finished(this);
			}
		}
	}

	/**
	 * 是否是被终止的。
	 * @return
	 */
	public boolean isTerminated(){
		return this.status == UserTask.TERMINATED;
	}
	
	public boolean isFinished(){
		return this.status == UserTask.FINISHED;
	}
	
	public boolean isStoped(){
		return this.isCanceld() || this.isTerminated() || this.isFinished();
	}
	
	public boolean isCanceld(){
		return this.status == UserTask.CANCEL;
	}
	
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getTotalJobs() {
		return totalJobs;
	}

	public void setTotalJobs(int totalJobs) {
		this.totalJobs = totalJobs;
	}

	public int getCompleteJobs() {
		return completeJobs;
	}

	public void setCompleteJobs(int completeJobs) {
		this.completeJobs = completeJobs;
		
		if(totalJobs != 0){
			this.setProgress(100 * (completeJobs / totalJobs));
		}
	}

	public void completeJobs(int jobCount){
		completeJobs = completeJobs + jobCount;
		
		if(totalJobs != 0){
			this.setProgress((int) (100 * (1d * completeJobs / totalJobs)));
		}
	}
	
	public void setData(String key, Object value){
		datas.put(key, value);
	}
	
	public Object getData(String key){
		return datas.get(key);				
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}
	
	public Object doAction(String name, Object ... parameters){
		return this.taskThing.doAction(name, actionContext, parameters);
	}
}
