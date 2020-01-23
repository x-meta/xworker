package xworker.notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.lang.system.message.Message;
import xworker.util.UtilData;


/**
 * 系统通知。
 * 
 * @author zyx
 *
 */
public class Notification implements java.lang.Comparable<Notification>{
	private static Logger logger = LoggerFactory.getLogger(Notification.class);
			
	private static long id = 0;
	public Thing thing;
	public ActionContext actionContext;
	private String label;
	protected long createTime = 0;
	protected long expireTime = 0;
	private long waitTime = 0;
	private boolean viewed = false;
	private Map<String, Object> data = null;
	private Object lockObj = new Object();
	private boolean sync;
	private int count = 0;
	private String messageId = null;
	private Map<String, Object> variables = null;
	private Message message;
	
	public Notification(Thing thing, ActionContext actionContext){
		this(thing, actionContext, System.currentTimeMillis());
	}
	
	public Notification(Thing thing, ActionContext actionContext, long newCreateTime){
		messageId = thing.doAction("getMessageId", actionContext);		
		
		init(thing, actionContext);
	}
	
	public Message getMessage() {
		return message;
	}

	public void init(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		
		message = new Message(thing, actionContext);
		
		//如果是持久化的，存储
		if(isPersistent()){
			save();
		}
		
		//要转移的变量
		variables = message.getVariables();
		label = thing.doAction("getLabel", actionContext, variables);
		sync = thing.doAction("isSync", actionContext);
		
		count++;
		viewed = false;
		createTime = System.currentTimeMillis();		
		Long timeout = (Long) thing.doAction("getTimeout", actionContext);
		
		if(timeout == null || (timeout != null && timeout <= 0)){
			expireTime = -1;
		}else{
			expireTime = createTime + (timeout * 1000);
		}
		
		/*
		 有了timeout，那么waittime似乎是多余的额
		Long time  = (Long) thing.doAction("getWaitTime", actionContext);		
		waitTime = createTime + (time * 1000);
		if(sync && expireTime <= 0) {
			expireTime = createTime * 30 * 1000;
		}*/
	}
	
	public Map<String, Object> getVariables(){
		return variables;
	}
	
	public boolean isSameMessage(String msgId) {
		return messageId != null && messageId.equals(msgId);
	}
	
	public long getWaitTime() {
		return waitTime;
	}
	
	public void lock() {
		synchronized(lockObj) {
			try {
				lockObj.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setViewed(){
		viewed = true;
	}
	
	public boolean isViewed(){
		return viewed;
	}
	
	public boolean isPersistent(){
		return UtilData.isTrue(thing.doAction("isPersistent", actionContext));
	}
	
	public String getLabel(){
		if(count <= 1) {
			return label;
		}else {
			return label + "(" + count + ")";
		}
	}
	
	public String getLevel(){
		Bindings bindings = actionContext.push();
		try {
			if(variables != null) {
				bindings.putAll(variables);
			}
			return (String) thing.doAction("getLevel", actionContext);
		}finally {
			actionContext.pop();
		}
	}
	
	public String getCategory(){
		String category =  thing.getStringBlankAsNull("category");
		if(category == null){
			return "";
		}else{
			return category;
		}
	}
	
	public String getCreateTimeLabel(){
		Date date = new Date(createTime);
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
		return sf.format(date);
	}
	
	public String getExpireTimeLabel(){
		if(expireTime > 0){
			Date date = new Date(expireTime);
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
			return sf.format(date);
		}else{
			return "N/A";
		}
	}
	
	public String[] getTableLabels(){
		String label = null;
		String category = getCategory();
		if(category != null && !"".equals(category)){
			label = category + ": " + getLabel();
		}else{
			label = getLabel();
		}
		
		return new String[]{
				label, getCreateTimeLabel(), getExpireTimeLabel()
		};
	}
	
	/**
	 * 持久化保存，直接把事物保存到_local事物管理器下。
	 */
	private void save(){
		id++;
		String name = "Notification_" + System.currentTimeMillis() + "_" + id;
		thing.saveAs("_local", "_local.xworker.notifications." + name);
	}
	
	public boolean isExpired(){
		if(expireTime > 0){
			return System.currentTimeMillis() > expireTime;
		}else{
			return false;
		}
	}
	
	public boolean isEndWait() {
		return sync && System.currentTimeMillis() > waitTime;
	}

	@Override
	public int compareTo(Notification o) {
		if(o.createTime > createTime){
			return -1;
		}else if(o.createTime < createTime){
			return 1;
		}
		
		return 0;
	}
	
	public void setData(String key , Object value) {
		if(data == null) {
			data = new HashMap<String, Object>();
		}
		
		data.put(key, value);
	}
	
	public Object getData(String key) {
		if(data != null) {
			return data.get(key);
		}else {
			return null;
		}
	}
	
	public boolean isSync() {
		return sync;
	}
	
	public void finish() {
		try {
			Bindings bindings = actionContext.push();
			try {
				if(variables != null) {
					bindings.putAll(variables);
				}
				thing.doAction("onFinished", actionContext, "notification", this);
			}finally {
				actionContext.pop();
			}
		}catch(Exception e) {
			logger.error("Notification onFinished error, thing=" + thing.getMetadata().getPath() , e);
		}finally {		
			finishWait();
		}
	}
	
	public void finishWait() {
		//激活等待的线程，如果请求是sync=true，那么请求线程会等待处理
		if(this.isSync()) {
			synchronized(lockObj) {
				lockObj.notifyAll();
			}
			
			sync = false;
		}
	}

	public String getMessageId() {
		return messageId;
	}
}
