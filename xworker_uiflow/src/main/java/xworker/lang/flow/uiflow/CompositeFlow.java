package xworker.lang.flow.uiflow;

import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilThing;

import xworker.lang.executor.Executor;
import xworker.util.XWorkerUtils;

public class CompositeFlow extends AbstractUiFlow{
	private static final String TAG = CompositeFlow.class.getName();
	
	Composite contentComposite;
	IFlow parent;
	
	public CompositeFlow(Composite contentComposite, Thing thing, ActionContext actionContext){
		super(thing, actionContext);
		this.contentComposite = contentComposite;
	}
	
	public CompositeFlow(CompositeFlow parent, Thing thing){
		super(thing, null);
		this.parent = parent;

		this.actionContext = new ActionContext();;	
		this.contentComposite = parent.contentComposite;
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
		
		start(nextNode);
	}

	@Override
	public void log(String message) {
		Executor.info(TAG, message);
	}

	@Override
	public void log(Throwable e) {
		Executor.error(TAG, "Error flow=" + thing.getMetadata().getPath(), e);
	}

	@Override
	public Object get(String key) {
		return actionContext.get(key);
	}

	@Override
	public void set(String key, Object value) {
		actionContext.g().put(key, value);
	}
	
	public ActionContext getActionContext(){
		return actionContext;
	}

	@Override
	public ActionContext runComposite(Thing flowNode, Thing composite) {
		return runComposite(flowNode, composite, null);
	}

	@Override
	public ActionContext runComposite(Thing flowNode, Thing composite,
			Map<String, Object> params) {
		for(Control child : contentComposite.getChildren()){
			child.dispose();
		}
		
		ActionContext ac = new ActionContext();
		ac.put("parent", contentComposite);
		ac.put("uiFlow", this);
		ac.put("parentContext", actionContext);
		ac.put("flowNode", flowNode);
		ac.put("nodeThing", flowNode);
		if(params != null){
			ac.putAll(params);
		}
		
		composite.doAction("create", ac);
		contentComposite.layout();		
		
		showOutlineDoc(flowNode);
		
		return ac;
	}
	
	
	/**
	 * 在Outline上显示事物的文档，如果outlineBrowser存在的话。
	 * @param thing
	 */
	public void showOutlineDoc(Thing thing){
		if(actionContext.get("outlineBrowser") != null){
			Browser outlineBrowser = actionContext.getObject("outlineBrowser");
			outlineBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing));
			
			Browser descOutlineBrowser = actionContext.getObject("descOutlineBrowser");
			descOutlineBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing.getDescriptor()));
		}
	}

	@Override
	public Object runAction(Thing action) {
		return action.doAction(UiFlow.ACTION_ACTION, actionContext, "uiFlow", this, "flowNode", action);	
	}

	@Override
	public void start(Thing nextNode) {
		if(nextNode == null){
			Executor.warn(TAG, "start node is null, flow=" + thing.getMetadata().getPath());
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
		return contentComposite;
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
		IFlow child = new CompositeFlow(this, childFlow);
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
