package xworker.swt.guide;

import java.util.Stack;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class GuideEntry {
	Thing thing;
	Thing currentNode;
	Stack<Thing> nodeHistory = new Stack<Thing>();
	
	public GuideEntry(Thing thing) {
		this.thing = thing;
	}
	
	/**
	 * 获取下一个节点，如果存在则同时设置为当前节点。
	 * 
	 * @param actionContext
	 * @return
	 */
	public Thing getNextNode(ActionContext actionContext) {
		if(currentNode == null) {
			if(thing.getChilds().size() > 0) {
				currentNode = thing.getChilds().get(0);				
			}
		}else {
			currentNode = currentNode.doAction("getNextNode", actionContext);
		}
		
		if(currentNode != null) {
			nodeHistory.push(currentNode);
		}
		
		return currentNode;
	}

	/**
	 * 获取前一个节点，如果存在则同时设置为当前节点。
	 * 
	 * @return
	 */
	public Thing getPreNode() {
		currentNode = null;
		if(nodeHistory.size() > 0) {
			nodeHistory.pop();
			if(nodeHistory.size() > 0) {
				currentNode = nodeHistory.peek();
			}			
		}
		
		return currentNode;
	}
		
	public Thing getCurrentNode() {
		return currentNode;
	}
	
	public void setCurrentNode(Thing node) {
		nodeHistory.push(node);
		this.currentNode = node;
	}

	public Thing getThing() {
		return thing;
	}
	
	
}
