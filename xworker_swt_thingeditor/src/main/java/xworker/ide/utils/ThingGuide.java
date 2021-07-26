package xworker.ide.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;
import org.xmeta.util.ActionContainer;

import xworker.util.XWorkerUtils;

public class ThingGuide {
	/** 作为SWT控件时的控件事物 */
	Thing controlThing;
	
	/** 向导事物 */
	Thing guideThing;
	
	/** 事物编辑器正在编辑的事物 */
	Thing editThing;
	
	ActionContext actionContext;
	
	/** 当前正在展示的向导节点 */	
	Thing currentNode;
	ActionContext currentActionContext;
	
	//节点调用栈
	Stack<Thing> callStack = new Stack<Thing>();
	
	/** 数据，可供向导节点之间传递数据 */
	Map<String, Object> datas = new HashMap<String, Object>();
	
	Composite contentComposite;
	Composite buttonComposite;
	Composite guideComposite; //向导的总Compsotie

	public ThingGuide(Thing editThing, Thing guideThing, ActionContext actionContext){
		this(null, editThing, guideThing, actionContext);
	}
	
	public ThingGuide(Thing controlThing, Thing editThing, Thing guideThing, ActionContext actionContext){
		this.controlThing = controlThing;
		this.editThing = editThing;
		this.guideThing = guideThing;
		this.actionContext = actionContext;
		actionContext.g().put("thingGuide", this);
		
		//先保存事物编辑器，由于向导有些操作可能是直接针对事物的，所以先保存避免一些编辑上的可能意外
		ActionContainer editActions = actionContext.getObject("actions");
		if(actionContext.get("currentModelContext") != null){
			//currentModelContext是用来判断是否之前正在编辑
			editActions.doAction("save", actionContext, "currentModel", new Thing());
		}
		
		//创建面板
		//Thing guideCompositeThing = World.getInstance().getThing("xworker.lang.util.ThingGuideShell/@guideComposite");		
		//guideComposite = guideCompositeThing.doAction("create", actionContext);
		
		contentComposite = actionContext.getObject("guideContentComposite");
		buttonComposite = actionContext.getObject("guideButtonComposite");
		
		//开始向导
		finishCurrentNode();
	}
	
	public void setNewGuideThing(Thing guideThing){
		this.guideThing = guideThing;
		this.currentNode = null;
		
		//开始向导
		finishCurrentNode();
	}
	
	public Thing getEditThing(){
		return editThing;
	}
	
	public Thing getGuideThing(){
		return guideThing;
	}
	
	public Thing getCurrentNode(){
		return currentNode;
	}
	
	public Composite getGuideComposite(){
		return guideComposite;
	}
	
	public void resetNode(){
		createNodeComposite();
	}
	
	public void reset(){
		currentNode = null;
		finishCurrentNode();
	}
	
	/**
	 * 结束当前节点，并进入下一个节点。
	 */
	public void finishCurrentNode(){
		if(currentNode != null){
			Object result = currentNode.doAction("nodeFinished", currentActionContext);
			
			//避免事物编辑器认为是外部修改了事物，在这类通过thingEntry.getThing()方法重新更新时间
			ThingEntry thingEntry = actionContext.getObject("thingEntry");
			if(thingEntry != null) {
				thingEntry.getThing();
			}
			
			if(result != null && result instanceof Boolean && (Boolean) result == false){
				//如果返回false，不进入下一个节点
				return;
			}
			
			if(result instanceof Thing){
				setNextNode((Thing) result);
				return;
			}
		}
		
		Thing nextNode = getNextNode();
		if(nextNode != null){
			setNextNode(nextNode);
		}else{
			//向导结束
			complete();
		}		
	}
		
	private void complete(){
		if(controlThing != null) {						
			//作为SWT控件使用
			ActionContext parentContext = actionContext.getObject(ActionContext.PARENT_CONTEXT);
			controlThing.doAction("complete", parentContext, "guide", this);
			
			if(this.contentComposite != null && this.contentComposite.isDisposed() == false) {
				//进入选择界面
				this.setNewGuideThing(World.getInstance().getThing("xworker.lang.util.ThingGuideSelector"));
			}
			
		}else {
			//在事物编辑器中
			returnToFormEditor();
		}
	}

	public void cancel(){
		cancel(true);
	}

	public void cancel(boolean returnToFormEditor){
		if(controlThing != null) {
			//作为SWT控件使用
			ActionContext parentContext = actionContext.getObject(ActionContext.PARENT_CONTEXT);
			controlThing.doAction("cancel", parentContext,"guide", this);
			
			if(this.contentComposite != null && this.contentComposite.isDisposed() == false) {
				//进入选择界面
				this.setNewGuideThing(World.getInstance().getThing("xworker.lang.util.ThingGuideSelector"));
			}
		}else {
			//在事物编辑器中
			if(returnToFormEditor) {
				returnToFormEditor();
			}
		}
	}
	
	public void returnToFormEditor(){
		//先保存事物编辑器
		ActionContainer editActions = actionContext.getObject("actions");
		//editActions.doAction("save");
		
		//返回表单编辑界面
		StackLayout layout = actionContext.getObject("editPartCompositeStackLayout");
		Composite form = actionContext.getObject("contentEditComposite");
		layout.topControl = form;
		Composite editPartComposite = actionContext.getObject("editPartComposite");
		editPartComposite.layout();
		
		//刷新
		//editActions.doAction("setThing", actionContext, "thing", editThing.getRoot());
		editActions.doAction("openThing", actionContext, "thing", editThing);
		editActions.doAction("refreshOutline", actionContext);	
		
		
		
	}
	
	/**
	 * 设置下一个节点。
	 * 
	 * @param node
	 */
	public void setNextNode(Thing node){
		currentNode = node;
		
		createNodeComposite();
	}
	
	/**
	 * 调用节点，执行指定节点的向导，把当前节点压入栈中，如果被调用的节点的向导执行完毕，那么回到先前的压入栈的节点。
	 * 
	 * @param node
	 */
	public void callNode(Thing node){
		if(node != null && currentNode != null){
			callStack.push(currentNode);
			
			setNextNode(node);
		}
	}
	
	/**
	 * 调用事物向导，新的事物向导执行完毕后还会继续执行当前向导。
	 * 
	 * 应该是由事物向导节点在nodeFinished事件中调用。。
	 * 
	 * @param thingGuide
	 */
	public void callThingGuide(Thing thingGuide){
		if(thingGuide != null && currentNode != null){
			Thing startNode = thingGuide.doAction("getNextNode", actionContext);
			if(startNode != null){
				callStack.push(currentNode);
				setNextNode(startNode);				
			}
		}
	}
		
	private void createNodeComposite(){
		ActionContainer editActions = actionContext.getObject("actions");
		
		//清除界面内容
		for(Control child : contentComposite.getChildren()){
			child.dispose();
		}
		for(Control child : buttonComposite.getChildren()){
			child.dispose();
		}
		
		Label titleLabel = actionContext.getObject("guideTitleLabel");
		Browser browser = null;
		if(editActions != null) {
			browser = editActions.getActionContext().getObject("outlineBrowser");
		}else if(controlThing != null) {
			//作为SWT控件使用
			ActionContext parentContext = actionContext.getObject(ActionContext.PARENT_CONTEXT);
			browser = controlThing.doAction("getBrowser", parentContext,"guide", this);
		}
		
		if(currentNode != null){
			//使用新的变量上下文创建节点的控件
			currentActionContext = new ActionContext();
			currentActionContext.put("parentContext", actionContext);
			currentActionContext.put("thingGuide", this);
			
			//创建按钮
			currentActionContext.peek().put("parent", buttonComposite);
			for(Thing buttons : currentNode.getChilds("Buttons")){
				for(Thing button : buttons.getChilds()){
					button.doAction("create", currentActionContext);
				}
			}
			
			//创建完成或下一步按钮
			Thing nextButton = World.getInstance().getThing("xworker.lang.util.ThingGuideShell/@18255/@nextButton");
			nextButton.doAction("create", currentActionContext);
			
			String nextButtonLabel = currentNode.getString("nextButtonLabel", null, currentActionContext);
			if(nextButtonLabel != null){
				((Button) currentActionContext.getObject("nextButton")).setText(nextButtonLabel);
			}
						
			currentActionContext.put("parent", contentComposite);					
			currentNode.doAction("create", currentActionContext);
			
			//标题和向导文档
			titleLabel.setText(currentNode.getMetadata().getLabel());
			if(browser != null){
				browser.setUrl(XWorkerUtils.getThingDescUrl(currentNode));
			}
			
			//初始化完毕的事件
			currentNode.doAction("inited", currentActionContext);
		}else{
			actionContext.peek().put("parent", contentComposite);
			Thing compositeThing = World.getInstance().getThing("xworker.lang.util.ThingGuideShell/@noNextNodeComposite");
			compositeThing.doAction("create", actionContext);
			
			titleLabel.setText(compositeThing.getMetadata().getLabel());
			if(browser != null){
				browser.setUrl(XWorkerUtils.getThingDescUrl(compositeThing));
			}
		}
		
		buttonComposite.getParent().layout();
		contentComposite.layout();
		buttonComposite.layout();
	}
	
	/**
	 * 如果向导节初始化错误，那么可以通过该方法简单的提示信息。
	 * 
	 * @param message
	 */
	public void showNodeInitedErrorMessage(String message){
		for(Control child : contentComposite.getChildren()){
			child.dispose();
		}
		currentActionContext.peek().put("", contentComposite);
		
		Thing browserThing = World.getInstance().getThing("xworker.lang.util.prototypes.MessageComposite/@messageBrowser");
		Browser browser = browserThing.doAction("create", currentActionContext);
		
		if(message != null){
			browser.setText(message);
		}
	}
	
	public void showNodeInitedErrorMessage(Thing messageThing){
		for(Control child : contentComposite.getChildren()){
			child.dispose();
		}
		currentActionContext.peek().put("", contentComposite);
		
		Thing browserThing = World.getInstance().getThing("xworker.lang.util.prototypes.MessageComposite/@messageBrowser");
		Browser browser = browserThing.doAction("create", currentActionContext);
		
		if(messageThing != null){
			browser.setText(XWorkerUtils.getThingDescUrl(messageThing));
		}
	}
	
	/**
	 * 默认的获取下一个演示节点的方法。
	 * 
	 * @return
	 */
	private Thing getNextNode(){
		if(currentNode == null){
			Thing node = World.getInstance().getThing(guideThing.getStringBlankAsNull("startNode"));
			if(node != null){
				return node;
			}
			
			List<Thing> nodes = guideThing.getChilds("ThingGuideNode");
			if(nodes.size() > 0){
				return nodes.get(0);
			}else{
				return null;
			}
		}else{
			Thing nextNode = (Thing) currentNode.doAction("getNextNode", currentActionContext);
			if(nextNode == null){
				return getCallerNextNode();
			}else{
				return nextNode;
			}
		}
	}
	
	private Thing getCallerNextNode(){
		if(callStack.size() > 0){
			currentNode = callStack.pop();
			Thing nextNode = (Thing) currentNode.doAction("getNextNode", currentActionContext);
			
			if(nextNode == null){
				return getCallerNextNode();
			}else{
				return nextNode;
			}
		}else{
			return null;
		}
	}
	
	public Map<String, Object> getDatas(){
		return datas;
	}
	
	public Object getData(String key){
		return datas.get(key);
	}
	
	public void setData(String key, Object value){
		datas.put(key, value);
	}
	
	public ActionContext getActionContext() {
		return actionContext;
	}
}

