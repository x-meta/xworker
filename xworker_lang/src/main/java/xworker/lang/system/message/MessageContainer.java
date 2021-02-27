package xworker.lang.system.message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 同一种类型的数据保存在一个容器中。
 * 
 * @author zyx
 *
 */
public class MessageContainer {
	Thing descriptor;
	
	List<Message> messages = new CopyOnWriteArrayList<Message>();
	
	int maxSize = 10;
	
	public MessageContainer(Thing descriptor) {
		this.descriptor = descriptor;
		
		int maxSize = descriptor.getInt("maxSize");
		if(maxSize > 0) {
			this.maxSize = maxSize;
		}
	}

	public Thing getDescriptor() {
		return descriptor;
	}
	
	public void addMessage(Message message) {
		messages.add(message);
		
		if(messages.size() > maxSize) {
			messages.remove(0);
		}
	}
	
	public void addMessage(Thing thing, ActionContext actionContext) {
		Message message = new Message(thing, actionContext);
		addMessage(message);
	}
	
	public List<Message> getMessages(){
		return messages;
	}
}
