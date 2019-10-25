package xworker.lang.flow.uiflow;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilThing;

/**
 * 动作流，简单化的UiFlow，它执行动作并根据返回值执行下一个动作。
 * 
 * @author zyx
 *
 */
public class ActionFlow extends AbstractUiFlow{
	private static Logger logger = LoggerFactory.getLogger(ActionFlow.class);
	
	ActionFlow parent;
	
	public ActionFlow(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}
	
	public ActionFlow(ActionFlow parent, Thing thing){
		super(thing, null);
		
		this.parent = parent;
		this.actionContext = new ActionContext();	
		this.actionContext.put("uiFlow", this);
		thing.doAction("intiParams", actionContext);
	}

	public void nodeFinished(Thing node, String nextConnectionName){
		//首先查找是否有对应的子节点
		Thing nextNode = null;
		for(Thing conn : node.getChilds("Connection")){
			if(nextConnectionName != null && !"".equals(nextConnectionName)){
				if(conn.getMetadata().getName().equals(nextConnectionName)){
					nextNode = UtilThing.getQuoteThing(conn, "nodeRef");
					break;
				}
			}else{
				//使用默认
				if(conn.getMetadata().getName().equals("default")){
					nextNode = UtilThing.getQuoteThing(conn, "nodeRef");
					break;
				}
			}
		}
		
		if(nextNode == null){
			nextNode = UtilThing.getQuoteThing(node, "nextNode");
		}
		
		nodeFinishedWithNextNode(node, nextNode);
	}
	
	private void nodeFinishedWithNextNode(Thing node, Thing nextNode){
		log("节点完成：" + node.getMetadata().getLabel() + ", path=" + node.getMetadata().getPath());
		if(nextNode == null){
			nextNode = UtilThing.getQuoteThing(node, "nextNode");
		}
		
		if(nextNode == null){
			log("流程结束:" + thing.getMetadata().getPath());
		}else{
			start(nextNode);
		}
	}
	
	@Override
	public void log(String message) {
		logger.info(message);
	}

	@Override
	public void log(Throwable e) {
		logger.error("Error flow=" + thing.getMetadata().getPath(), e);
	}

	@Override
	public Object get(String key) {
		return actionContext.g().get(key);
	}

	@Override
	public void set(String key, Object value) {
		actionContext.g().put(key, value);
	}

	@Override
	public ActionContext runComposite(Thing flowNode, Thing composite) {
		throw new ActionException("ActionFlow not suuport SWT");
	}

	@Override
	public ActionContext runComposite(Thing flowNode, Thing composite,
			Map<String, Object> params) {
		throw new ActionException("ActionFlow not suuport SWT");
	}

	@Override
	public Object runAction(Thing action) {
		return action.doAction(UiFlow.ACTION_ACTION, actionContext, "uiFlow", this, "flowNode", action);	
	}

	@Override
	public void start(Thing nextNode) {
		if(nextNode == null){
			logger.warn("start node is null, flow=" + thing.getMetadata().getPath());
		}else{
			nextNode.doAction(UiFlow.NODE_ACTION, actionContext, "uiFlow", this, "flowNode", nextNode);
		}
	}

	@Override
	public void start() {
		start(getStartNode());
	}

	@Override
	public void end() {
		if(parent != null){
			//初始化到父节点的参数
			thing.doAction("setReturnValues", actionContext);
			
			//返回值
			String returnString = (String) thing.doAction("getRetunString", actionContext);
			
			//调用父节点的节点结束
			parent.nodeFinished(thing, returnString);
		}else{
			return;
		}
	}

	@Override
	public Composite getMainComposite() {
		return null;
	}
	
	public Thing getStartNode(){
		for(Thing node : thing.getChilds()){
			if("Start".equals(node.getThingName())){
				return node;
			}
		}
		
		if(thing.getChilds().size() > 0){
			return thing.getChilds().get(0);
		}
		
		return null;
	}

	@Override
	public void startChildFlow(Thing childFlow) {
		ActionFlow child = new ActionFlow(this, childFlow);
		child.start();
	}

	@Override
	public IFlow getParent() {		
		return parent;
	}

	@Override
	public Thing getThing() {
		return thing;
	}

}
