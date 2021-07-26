package xworker.lang.executor;

import java.util.*;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import xworker.util.UtilData;

public class Request {
	public static final String NORMAL = "normal";
	public static final String URGENT = "urgent";

	public static final String PLATFORM_SWT = "swt";
	public static final String PLATFORM_JAVAFX = "javafx";

	Thing thing;
	ActionContext parentContext;
	ActionContext actionContext;
	Map<String, Object> variables;
	ExecutorService executorService;
	ExecutorService uiExecutorService;
	Map<String, Object> datas = new HashMap<String, Object>();
	Date createDate = new Date();
	int count = 1;
	String priority;
	boolean finished = false;
	boolean readed = false;
	List<RequestListener> listeners = new ArrayList<>();
	String label;
	String requestId;

	public Request(Thing thing, ActionContext parentContext) {
		this.thing = thing;
		this.parentContext = parentContext;
		this.actionContext = new ActionContext();
		actionContext.put("parentContext", parentContext);
		this.variables = thing.doAction("getVariables", parentContext);
		if(variables == null) {
			variables = new HashMap<String, Object>();
		}
		variables.put("request", this);
		this.priority = thing.getStringBlankAsNull("priority");
		actionContext.putAll(variables);
		label = thing.doAction("getLabel", parentContext);
		requestId = thing.doAction("getRequestId", parentContext);
		if(requestId == null || requestId.isEmpty()){
			requestId = thing.getMetadata().getPath();
		}
	}

	public boolean isTimeout(){
		long timeout = thing.getLong("timeout");
		return timeout > 0 && (System.currentTimeMillis() - createDate.getTime()) > (timeout * 60000);
	}

	public void timeout(){
		thing.doAction("timeout", actionContext);

		this.finish();
	}

	public void addListener(RequestListener listener){
		if(!listeners.contains(listener)){
			listeners.add(listener);
		}
	}

	public void removeListener(RequestListener listener){
		listeners.remove(listener);
	}

	public void setUIExecutorService(ExecutorService executorService) {
		this.uiExecutorService = executorService;
	}
	
	public void setData(String key, Object value) {
		datas.put(key, value);
	}
	
	public Object getData(String key) {
		return datas.get(key);
	}
	
	public Object doAction(String name) {
		return thing.doAction(name, actionContext);
	}
	
	public Object doAction(String name, Map<String, Object> params) {
		Bindings bindings = actionContext.push();
		try {
			if (params != null) {
				bindings.putAll(params);
			}

			return thing.doAction(name, actionContext);
		}finally {
			actionContext.pop();
		}
	}
	
	public Object doAction(String name, Object ... params) {
		return thing.doAction(name, actionContext, params);
	}
	
	public void finish() {
		if(!finished){
			for(RequestListener listener : listeners) {
				listener.finished(this);
			}
		}

		finished = true;

		if(uiExecutorService != null) {
			uiExecutorService.removeRequest(this);
		}else if(executorService != null){
			executorService.removeRequest(this);
		}
	}

	public String getLabel(){
		if(label == null || label.isEmpty()){
			label = thing.getMetadata().getLabel();
		}
		return label;
	}

	public Object getVariable(String key) {
		return variables.get(key);
	}

	public boolean isSupport(String platform){
		return UtilData.isTrue(thing.doAction("isSupport", actionContext));
	}

	/**
	 * 创建界面。
	 *
	 * @param parent
	 * @param platform
	 * @return
	 */
	public Object create(Object parent, String platform){
		setReaded();
		return thing.doAction("create", actionContext, "platform", platform, "parent", parent);
	}

	/**
	 * 设置应用已读。
	 */
	public void setReaded(){
		if(!readed){
			readed = true;

			for(RequestListener listener : listeners) {
				listener.readed(this);
			}
		}
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}
	
	public void putVariable(String key , Object value) {
		variables.put(key, value);
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public ExecutorService getUiExecutorService() {
		return uiExecutorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public Map<String, Object> getDatas() {
		return datas;
	}

	public ActionContext getParentContext() {
		return parentContext;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count){
		this.count = count;
	}

	/**
	 * 返回紧急程度。如normal或urgent。
	 * @return
	 */
	public String getPriority() {
		return priority;
	}

	public boolean isFinished() {
		return finished;
	}

	public void addCount(){
		count++;
	}

	public boolean isReaded() {
		return readed;
	}

	public boolean equals(Request request){
		if(request == null){
			return false;
		}

		if(request.thing == thing && request.requestId.equals(requestId)){
			return true;
		}else{
			return false;
		}
	}

	public boolean equals(Thing thing, ActionContext actionContext){
		if(this.thing != thing){
			return false;
		}

		String requestId = thing.doAction("getRequestId", actionContext);
		if(requestId == null || requestId.isEmpty()){
			requestId = thing.getMetadata().getPath();
		}

		if(this.requestId.equals(requestId)){
			return true;
		}else{
			return false;
		}
	}
}
