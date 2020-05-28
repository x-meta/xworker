package xworker.util.autoplay;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

public class AutoPlayNodeGroup implements AutoPlayNode{
	static final String TAG = AutoPlayNodeGroup.class.getName();
	
	Thing thing;
	ActionContext actionContext;
	long delay = 0;
	List<AutoPlayNode> nodes = new ArrayList<AutoPlayNode>();
	/** 当前的节点的位置索引 */
	int nodeIndex = 0;
	AutoPlayListener listener = null;
	AutoPlayNodeGroup parent = null;
	
	public AutoPlayNodeGroup(AutoPlayNodeGroup parent, Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;		
		this.delay = thing.doAction("getDelay", actionContext);
		this.parent = parent;
		
		for(Thing child : thing.getChilds()) {
			AutoPlayNode node = child.doAction("create", actionContext, "parent", this);
			if(node != null) {
				nodes.add(node);
			}
		}
	}
	
	public void setListener(AutoPlayListener listener) {
		this.listener = listener;
	}
	
	public AutoPlayListener getListener() {
		if(this.listener != null) {
			return this.listener;
		} else if(parent != null) {
			return parent.getListener();
		} else {
			return null;
		}
	}
	public boolean hasNext() {
		if(nodeIndex < nodes.size()) {
			AutoPlayNode node = nodes.get(nodeIndex);
			if(node instanceof AutoPlayNodeGroup) {
				AutoPlayNodeGroup group = (AutoPlayNodeGroup) node;
				if(group.hasNext()) {
					return true;
				}else {
					nodeIndex++;
					return hasNext();
				}
			}else {
				return true;
			}
		}else {
			return false;
		}			
	}
	
	/**
	 * 返回一个要执行的AutoPlayNode，不包含AutoPlayNodeGroup。
	 * @return
	 */
	public AutoPlayNode getNextNode() {
		if(hasNext()) {
			AutoPlayNode node = nodes.get(nodeIndex);
			if(node instanceof AutoPlayNodeGroup) {
				AutoPlayNodeGroup group = (AutoPlayNodeGroup) node;
				return group.getNextNode();
			}else {
				return node;
			}
		}else {
			return null;
		}
	}
	
	/**
	 * 返回执行的栈列表。即组-&gt;组-&gt;...-&gt;节点。
	 * 
	 * @return
	 */
	public List<AutoPlayNode> getExecuteStacks(){
		AutoPlayNode node = getNextNode();
		List<AutoPlayNode> nodes = new ArrayList<AutoPlayNode>();
		if(node != null) {			
			nodes.add(node);
			while(node.getParnet() != null) {
				node = node.getParnet();
				nodes.add(0, node);
			}
		}
		
		return nodes;
	}
	
	private boolean executeNode(boolean step) {
		AutoPlayNode node = nodes.get(nodeIndex);
		try {			
			node.run(step);
			
			if(node instanceof AutoPlayNodeGroup) {
				AutoPlayNodeGroup group = (AutoPlayNodeGroup) node;
				if(!group.hasNext()) {
					nodeIndex++;
				}
			} else {
				nodeIndex++;
			}
			
			return true;
		}catch(Exception e) {
			Executor.error(TAG, "Execute node error, thing=" + thing.getMetadata().getPath(), e);
			return false;
		}
		
	}
	
	@Override
	public long getDelay() {
		return delay;
	}

	public AutoPlay getAutoPlay() {
		AutoPlayNodeGroup group = this;
		while(!(group instanceof AutoPlay)){
			group = group.getParnet();
			if(group == null) {
				break;
			}
		}
		
		return (AutoPlay) group;
	}
	
	@Override
	public void run(boolean step) {
		AutoPlayListener listener = this.getListener();
		AutoPlay autoPlay = getAutoPlay();
		
		if(step) {
			if(hasNext()) {
				AutoPlayNode nextNode = getNextNode();
				if(listener != null) {
					listener.beforeExecute(autoPlay, nextNode);
				}
				if(executeNode(step)) {				
					if(listener != null) {
						listener.executed(autoPlay, nextNode, true);
					}
				} else {
					if(listener != null) {
						listener.executed(autoPlay, nextNode, false);
					}
				}
			}
		}else {
			while(hasNext()) {
				AutoPlayNode nextNode = getNextNode();
				if(listener != null) {
					listener.beforeExecute(autoPlay, nextNode);
				}
				
				try {
					Thread.sleep(nextNode.getDelay());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(!executeNode(step)) {
					//执行失败中断
					if(listener != null) {
						listener.executed(autoPlay, nextNode, false);
					}
					
					break;
				}else {
					if(listener != null) {
						listener.executed(autoPlay, nextNode, true);
					}
				}
			}
		}
	}
	
	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}
	
	@Override
	public AutoPlayNodeGroup getParnet() {
		return parent;
	}
	
	public static AutoPlayNodeGroup create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object parent = actionContext.get("parent");
		if(parent instanceof AutoPlayNodeGroup) {
			return new AutoPlayNodeGroup((AutoPlayNodeGroup) parent, self, actionContext);
		}else {
			return new AutoPlayNodeGroup(null, self, actionContext);
		}
	}
}
