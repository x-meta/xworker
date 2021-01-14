package xworker.lang.system.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumerTreeItem {
	String name;
	
	/** 子节点 */
	Map<String, ConsumerTreeItem> items = new HashMap<String, ConsumerTreeItem>();
	
	/** 订阅者列表 */
	List<MessageConsumer> comsumers = new ArrayList<MessageConsumer>();
	
	ConsumerTreeItem parent;
	
	public ConsumerTreeItem(String name, ConsumerTreeItem parent) {
		this.name = name;
		this.parent = parent;
	}
	
	public String toString() {
		String str = name;
		ConsumerTreeItem parent = this;
		while(parent.parent != null) {
			parent = parent.parent;
			str = parent.name + "/" + str;
		}
		
		return str;
	}
	
}
